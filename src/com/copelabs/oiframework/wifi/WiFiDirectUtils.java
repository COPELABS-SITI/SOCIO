package com.copelabs.oiframework.wifi;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.copelabs.oiframework.contentmanager.Packet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class WiFiDirectUtils implements ConnectionInfoListener, PeerListListener, Callback{
	
    //TXT RECORD properties
    public static final String SERVICE_INSTANCE = "_oiframework";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final String KEY_MAC = "key_mac";
    
    public static final int socket_port = 4545;
	
	private Context mContext;
	private WiFiDirect mCallback;
	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
	public static final int WIFI_IDLE = 0;
	public static final int WIFI_CONNECTING = 1;
	public static final int WIFI_CONNECTED = 2;
	public static final int WIFI_DISCONNECTED = 3;
    public int WiFiStatus = WIFI_IDLE;
    
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int SOCKETERROR = 0x400 + 2;
    public static final int EMPTY = 0x400 + 3;
    
    private String mSendingToDevice;
	
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private WifiP2pDnsSdServiceRequest serviceRequest;
	private WifiP2pDnsSdServiceInfo service;
	private WifiDirectAutoAccept mAutoAccept;
    private boolean isWifiP2pEnabled = false;
    public ArrayList<WiFiDirectDevice> mAvailableDevices= new ArrayList<WiFiDirectDevice>();
    private Map<String, List<Packet>> mToSend = new HashMap<String, List<Packet>>();
    private Map<String, String> txtRecordMapReceived;
    private Handler handler = new Handler(this);
    
    //Info announced
    private String gMAC; 
    private Map<String, String> gListOfSocialWeight;
        
    // Timer
    private Timer timer;
    private TimerTask timerTask;
    private static final int TIMER_SCHEDULED = 0;
    private static final int TIMER_IDLE = 1;
    private static final int TIMER_RUNNING = 2;
    private int TimerStatus = TIMER_IDLE;
    
    // TODO: Check this MAC address. Improve it if possible
    public String mThisDeviceMACP2p;
    
	public WiFiDirectUtils (Context mContext, WiFiDirect mCallback) {
		this.mContext = mContext;
		this.mCallback = mCallback;
		// add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(mContext, mContext.getMainLooper(), null);
        
        /** register the BroadcastReceiver with the intent values to be matched */
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        mContext.registerReceiver(receiver, intentFilter);
	}
	
	// PUBLIC METHODS ************************************************************************
	
	public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

	
	/**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
        if (!isWifiP2pEnabled) {
        	notifyError(0);
        } else {
        	notifyError(1);
        }
        
    }
    
    /**
     * @param isWifiP2pEnabled get isWifiP2pEnabled flag
     */
    public boolean getIsWifiP2pEnabled() {
        return isWifiP2pEnabled;
    }
	
    /** 
     * Get the MAC address of WiFi P2P interface.
     * Only returns a MAC address if this device is a group owner.
     * @return WiFi P2P interface MAC address
     */
    public String getWFDirectMacAddress(){
    	try {
    	    List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
    	    for (NetworkInterface ntwInterface : interfaces) {
    	        if (ntwInterface.getName().equalsIgnoreCase("p2p-wlan0-0")) {
    	            byte[] byteMac = ntwInterface.getHardwareAddress();
    	            if (byteMac==null){
    	                return null;
    	            }
    	            StringBuilder strBuilder = new StringBuilder();
    	            for (int i=0; i<byteMac.length; i++) {
    	                strBuilder.append(String.format("%02X:", byteMac[i]));
    	            }
    	            if (strBuilder.length()>0){
    	                strBuilder.deleteCharAt(strBuilder.length()-1);
    	            }
    	            return strBuilder.toString();
    	        }
    	    }
    	} catch (Exception e) {
    	    Log.e(WiFiDirect.TAG, e.getMessage());
    	}
    	return "";
    }
    
    /**
     * Get the MAC address of WiFi interface.
     * @return WiFi MAC address
     */
    public String getWiFiMacAddress() {
    	WifiManager wifiMan = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    	if (wifiMan == null)
    		return null;
    	if (!wifiMan.isWifiEnabled())
    		return null;
    	WifiInfo wifiInf = wifiMan.getConnectionInfo();
    	return wifiInf.getMacAddress();
    }
    
    /**
     * Starts WiFi P2P mechanisms to discover peers and 
     * also to register this framework as an available service.
     */
	public void start(final String mMAC, final Map<String, String> mListOfSocialWeight) {
		
		gMAC = mMAC;
		gListOfSocialWeight = mListOfSocialWeight;
		
		//TODO:TESTE
		Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
            	if (WiFiStatus == WIFI_DISCONNECTED)
            		restartDiscovery();
            }

        },
        //Set how long before to start calling the TimerTask (in milliseconds)
        60000,
        //Set the amount of time between each execution (in milliseconds)
        60000);
		
      //TODO:TESTE
		Timer t2 = new Timer();
		  //Set the schedule function and rate
		  t2.scheduleAtFixedRate(new TimerTask() {
		
		      @Override
		      public void run() {
		    	  ArrayList<WiFiDirectDevice> mToRemove = new ArrayList<WiFiDirectDevice>();
		    	  for (WiFiDirectDevice mDevice : mAvailableDevices) {
		    		long mTimeNow = System.currentTimeMillis();
		          	long mTimePassed = mTimeNow - mDevice.getLastTimeSeen();
		  			if (mTimePassed > 90000) {
		  				Log.i(WiFiDirect.TAG, mDevice.getDevice().deviceName + " device disappear. Removing device...");
		  				mToRemove.add(mDevice);
		  				notifyOnWiFiDirectDeviceDisappear(mDevice);
		  			} else {
						Log.i(WiFiDirect.TAG, mDevice.getDevice().deviceName + " device found. Time from last seen: " + mTimePassed);
		  			}
		    	  }
		    	  mAvailableDevices.removeAll(mToRemove);
		      }
		
		  },
		  //Set how long before to start calling the TimerTask (in milliseconds)
		  70000,
		  //Set the amount of time between each execution (in milliseconds)
		  70000);
        
        
        
		if (!isWifiP2pEnabled) {
			Log.w(WiFiDirect.TAG, "Wi-Fi Disabled. Please turn Wi-Fi on.");
			return;
		}
		manager.clearLocalServices(channel, new ActionListener() {
			@Override
			public void onSuccess() {
				Log.i(WiFiDirect.TAG, "Local services cleared");
				startRegistrationAndDiscovery(mMAC, mListOfSocialWeight);
				
			}
			@Override
			public void onFailure(int reason) {
				 Log.e(WiFiDirect.TAG, "Failed to clean local services. Unable to start resgistration process.");
			}
		});
		
	}
	
	/**
	 * Stops WiFi P2P mechanism. If a group is formed, it's removed.
	 */
	public void stop() {
		disconnectP2p();
		
		if(manager == null) return;
		if(channel == null) return;
		if(service == null) return;
		manager.removeLocalService(channel, service, null);
		
		if(serviceRequest == null) return;
		manager.removeServiceRequest(channel, serviceRequest, null);
		
		if (receiver == null) return;
        mContext.unregisterReceiver(receiver);
    }
	
	public void wifiWasEnabled() {
		if (gMAC == null)
			return;
		startRegistrationAndDiscovery(gMAC, gListOfSocialWeight);
	}
	
	public void wifiDisabled() {
		mAvailableDevices.clear();
		if (manager == null)
			return;
		if (channel == null)
			return;
		if (service != null)
			manager.removeLocalService(channel, service, null);
		if (serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest, null);
	}
	
	// WIFI DIRECT REGISTRATION AND DISCOVERY ***************************************
	
	/**
     * Registers a local service and then initiates a service discovery
     */
    private void startRegistrationAndDiscovery(String mMAC, Map<String, String> mListOfSocialWeight) {
    	registration(mMAC, mListOfSocialWeight);
        discoverService();
    }
    
    public void restartDiscovery() {
    	if (serviceRequest != null) {
    		if (channel == null)
    			return;
    		Log.i(WiFiDirect.TAG,"Restarting service request");
            manager.removeServiceRequest(channel, serviceRequest,
                    new ActionListener() {
                        @Override
                        public void onSuccess() {
                        	Log.i(WiFiDirect.TAG,"Service Request removed");
                        	discoverService();
                        }
                        @Override
                        public void onFailure(int arg0) {
                        	Log.e(WiFiDirect.TAG,"Service Request remove failed");
                        	discoverService();
                        }
                    });
    	}
    }
    
    public void updateRegistration (final String mMAC, final Map<String, String> mListOfSocialWeight) {
    	if (manager == null)
    		return;
    	if (channel == null)
    		return;
    	if (service == null) {
    		Log.w(WiFiDirect.TAG, "Update registration failed because service is null. Cleaning all local services...");
    		manager.clearLocalServices(channel, new ActionListener() {
				@Override
				public void onSuccess() {
					registration(mMAC, mListOfSocialWeight);
				}
				@Override
				public void onFailure(int reason) {
					Log.e(WiFiDirect.TAG, "Error removing all Local Services. Unable to update local services.");
				}  			
    		});
    		
    	}
    	
    	manager.removeLocalService(channel, service, new ActionListener() {
			@Override
			public void onSuccess() {
				registration(mMAC, mListOfSocialWeight);
			}
			@Override
			public void onFailure(int reason) {
				//TODO: Check if this happens. If so, how to improve it.
				Log.e(WiFiDirect.TAG, "Error removing Local Service. Unable to update local services.");
			}
    	});
    }
    
    private void registration(String mMAC, Map<String, String> mListOfSocialWeight) {
    	if (mMAC == null) {
    		Log.w(WiFiDirect.TAG, "MAC address field is null. No announcement was made.");
    	}
    	if (mListOfSocialWeight == null) {
    		Log.w(WiFiDirect.TAG, "List of Social Weight is null. No announcement was made.");
    	}
    			
		final Map<String, String> record = new HashMap<String, String>();
		
        record.put(KEY_MAC, mMAC);
        
        for(Entry<String, String> entry : mListOfSocialWeight.entrySet()) {
            String mMACneighbor = entry.getKey();
            String mSocialWeightNeighbor = entry.getValue();
            record.put(mMACneighbor, mSocialWeightNeighbor);
            if (record.size() > 6) {
            	Log.w(WiFiDirect.TAG,"Maximum TXT size reached. Some devices were ignored.");
            	break;
            }
        }
		
        service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.clearLocalServices(channel, new ActionListener() {
            @Override
            public void onSuccess() {
            	Log.i(WiFiDirect.TAG,"All Local service removed");
            }
            @Override
            public void onFailure(int error) {
            	Log.e(WiFiDirect.TAG,"Failed to clear all local services");
            }
        });
        
        gMAC = mMAC;
		gListOfSocialWeight = mListOfSocialWeight;
		
        manager.addLocalService(channel, service, new ActionListener() {
            @Override
            public void onSuccess() {
            	Log.i(WiFiDirect.TAG,"Added Local Service");
            }
            @Override
            public void onFailure(int error) {
            	Log.e(WiFiDirect.TAG,"Failed to add a local service. List size: " + record.size());
            }
        });
	}
    
    public void discoverService() {
        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
        manager.setDnsSdResponseListeners(channel,
                new DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                            String registrationType, WifiP2pDevice srcDevice) {
                        
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                        	if (txtRecordMapReceived == null)
                        		return;
                        	try {
                        	if (!txtRecordMapReceived.get("TxTDeviceAddress").equalsIgnoreCase(srcDevice.deviceAddress)) {
                        		Log.i(WiFiDirect.TAG, "Received TxT record is not from device " + srcDevice.deviceName);
                        		txtRecordMapReceived = null;
                        		return;
                        	}
                        	}catch (Exception e) {
                        		Log.e(WiFiDirect.TAG, "Device " + srcDevice.deviceName + " found but no TXT received.");
                        		return;
                        	};
                        	txtRecordMapReceived.remove("TxTDeviceAddress");
                        	for (WiFiDirectDevice mDevice : mAvailableDevices) {
    							if (mDevice.getDevice().deviceAddress.equalsIgnoreCase(srcDevice.deviceAddress)) {
    								Log.i(WiFiDirect.TAG, srcDevice.deviceName + " device already added.");
    								mDevice.setLastTimeSeen(System.currentTimeMillis());
    								return;
    							}
    						}
                        	WiFiDirectDevice mDevice = new WiFiDirectDevice();
                        	mDevice.setDevice(srcDevice);
                        	mDevice.setInstanceName(instanceName);
                        	mDevice.setServiceRegistrationType(registrationType);
                        	mDevice.setLastTimeSeen(System.currentTimeMillis());
                        	mAvailableDevices.add(mDevice);
                        	notifyOnNewWiFiDirectDeviceFound(mDevice, txtRecordMapReceived);
                        	txtRecordMapReceived = null;
                        }
                    }
                }, new DnsSdTxtRecordListener() {
                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */

					@Override
					public void onDnsSdTxtRecordAvailable(
							String fullDomainName,
							Map<String, String> txtRecordMap,
							WifiP2pDevice srcDevice) {
						Log.d(WiFiDirect.TAG, srcDevice.deviceAddress + " " + srcDevice.deviceName + " TXT record received");
						txtRecordMapReceived = txtRecordMap;
						txtRecordMapReceived.put("TxTDeviceAddress", srcDevice.deviceAddress);
					}
                });
        
        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {
                    @Override
                    public void onSuccess() {
                    	Log.i(WiFiDirect.TAG,"Added service discovery request");
                    }
                    @Override
                    public void onFailure(int arg0) {
                        Log.e(WiFiDirect.TAG,"Failed adding service discovery request");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {
            @Override
            public void onSuccess() {
            	Log.i(WiFiDirect.TAG,"Service discovery initiated");
            }
            @Override
            public void onFailure(int arg0) {
            	Log.e(WiFiDirect.TAG,"Service discovery failed");
            }
        });
        
        /*//Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
            	startDiscoverPeers();
            }

        },
        //Set how long before to start calling the TimerTask (in milliseconds)
        0,
        //Set the amount of time between each execution (in milliseconds)
        60000);*/
        //startDiscoverPeers();
        
    }
    
    private void startDiscoverPeers() {
    	if (manager == null)
    		return;
    	manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				Log.i(WiFiDirect.TAG,"Peers discovery initiated");
			}
			
			@Override
			public void onFailure(int reason) {
				Log.e(WiFiDirect.TAG,"Peers discovery failed");
			}
		});
    }
    
    /**
     * PeerListListener
     * Receives all peers available by WiFi P2P.
     * @param peers list of peers available
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.d(WiFiDirect.TAG,String.format("PeerListListener: %d peers available,"
        		+ " updating device list", peers.getDeviceList().size()));
        //TODO: Check what to do with this list.
        for (WifiP2pDevice mDevice : peers.getDeviceList()) {
    		Log.i(WiFiDirect.TAG, "Peer Available: " + mDevice.deviceName);
    	}
        
        
    }
    

    /*
    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            retryChannel = true;
            manager.initialize(mContext, mContext.getMainLooper(), this);
        } else {
            Toast.makeText(mContext,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }
    */
    
    private void checkNextPacketQueue(String mDeviceMAC) {
    	if (mDeviceMAC == null) {
    		Log.i(WiFiDirect.TAG, "Connection from other device finished.");
    		TimerStatus = TIMER_IDLE;
    		//mAvailableDevices.clear();
    		discoverService();
    		return;
    	}
    	if (mDeviceMAC.isEmpty()) {
    		Log.i(WiFiDirect.TAG, "Connection from other device finished.");
    		TimerStatus = TIMER_IDLE;
    		//TODO: Check this!!! When this node receives data to others, it must clear the available array, if not already added msg appears.
    		//mAvailableDevices.clear();
    		discoverService();
    		return;
    	}
    	Log.i(WiFiDirect.TAG, "List of Packets for device " + mDeviceMAC + " removed from the queue.");
    	mToSend.remove(mDeviceMAC);
    	//TODO: Improve this flag
    	mSendingToDevice = null;
    	if (mToSend.size() > 0) {
    		Log.i(WiFiDirect.TAG, "Next in Queue - Sending packets to "+ mToSend.entrySet().iterator().next().getKey());
    		Entry<String, List<Packet>> mNextTx = mToSend.entrySet().iterator().next();
    		timer = null;
    		startTimer(mNextTx.getKey());
    	} else {
    		Log.i(WiFiDirect.TAG, "Next in Queue - Nothing to do. Queue empty");
    		TimerStatus = TIMER_IDLE;
    		timer = null;
    		discoverService();
    	}
    }

    
    // CONNECTIVITY ************************************************************************
    
    public boolean makeConnection(WiFiDirectDevice mDevice, ArrayList<Packet> mPackets) {
    	
    	if (!getIsWifiP2pEnabled())
    		return false;
    	if(!mAvailableDevices.contains(mDevice))
    		return false;
    	
    	// Check if Queue has already packets to send to this device.
    	// If yes, we just replace the old list of packets by the new list received.
    	if (mToSend.containsKey(mDevice.getDevice().deviceAddress))
    		mToSend.remove(mDevice.getDevice().deviceAddress);
    		    	
    	mToSend.put(mDevice.getDevice().deviceAddress, mPackets);
    	Log.i(WiFiDirect.TAG, "Packets to " + mDevice.getDevice().deviceAddress + " added to queue.");
    	
    	if (TimerStatus == TIMER_IDLE) 
    		startTimer(mDevice.getDevice().deviceAddress);
    	else
    		Log.i(WiFiDirect.TAG, "Timer already running or scheduled, only added to queue.");
    	return true;
    }
        
    private void connectP2p(final String mDeviceMAC) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = mDeviceMAC;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0; // Force the other device to be a GO.
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new ActionListener() {
                        @Override
                        public void onSuccess() {
                        	Log.i(WiFiDirect.TAG,"Service Request removed");
                        }
                        @Override
                        public void onFailure(int arg0) {
                        	Log.e(WiFiDirect.TAG,"Service Request remove failed");
                        }
                    });
        manager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(WiFiDirect.TAG,"Connecting to device " + mDeviceMAC);
                mSendingToDevice = mDeviceMAC;
                WiFiStatus = WIFI_CONNECTING;
            }
            @Override
            public void onFailure(int errorCode) {
            	//TODO: If failed, it is necessary to try it later. No just tries one time, if failed remove entry from the queue.
            	Log.e(WiFiDirect.TAG,"Failed connecting to device " + mDeviceMAC);
            	//checkNextPacketQueue(mDeviceMAC);
            	mSendingToDevice = mDeviceMAC;
            	dealWithConnectionEnded(true);
            }
        });
    }
    
    private void disconnectP2p() {
    	if (manager != null && channel != null) {
            manager.removeGroup(channel, new ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(WiFiDirect.TAG, "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {
                }

            });
        }
    }
    
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = null;
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */
        if (TimerStatus == TIMER_SCHEDULED) {
    		stopTimer();
        }
        if (p2pInfo.isGroupOwner) {
            Log.i(WiFiDirect.TAG, "Connected as group owner");
            try {
            	//TODO: Start ServerSocket to exchange data
                //handler = new GroupOwnerSocketHandler(getHandler(), mToSend);
				if (serviceRequest != null)
					manager.removeServiceRequest(channel, serviceRequest,
							new ActionListener() {
				                 @Override
				                 public void onSuccess() {
				                 	Log.i(WiFiDirect.TAG,"Service Request removed");
				                 }
				                 @Override
				                 public void onFailure(int arg0) {
				                 	Log.e(WiFiDirect.TAG,"Service Request remove failed");
				                 }
				             });
            	handler = new ServerThread(getHandler(), mToSend);
                handler.start();
            } catch (IOException e) {
                Log.e(WiFiDirect.TAG,"Failed to create a server thread - " + e.getMessage());
                disconnectP2p();
                return;
            }
        } else {
            Log.i(WiFiDirect.TAG, "Connected as peer");
            //TODO: Start ClientSocket to exchange data
            handler = new ClientSocketHandler(getHandler(), p2pInfo.groupOwnerAddress, mToSend.get(mSendingToDevice), mThisDeviceMACP2p);
            handler.start();
        }
        
    }
    
    public void dealWithConnectionEnded (boolean failed) {
    	if (failed) {
    		//TODO: We must inform upper layers that this device is not available.
    		for (WiFiDirectDevice mDevice : mAvailableDevices) {
				if (mDevice.getDevice().deviceAddress.equalsIgnoreCase(mSendingToDevice)) {
					Log.i(WiFiDirect.TAG, mSendingToDevice + " removed from available device.");
					mAvailableDevices.remove(mDevice);
					break;
				}
			}
    	}
    	checkNextPacketQueue(mSendingToDevice);
    }
    
    // TIMER *******************************************************************************
    
    public void startTimer(String mDeviceMAC) {
    	
    	if (timer != null || TimerStatus == TIMER_SCHEDULED) {
    		Log.e(WiFiDirect.TAG, "Timer already running. Start Timer cancelled.");
    		return;
    	}
    	
        //set a new Timer
    	TimerStatus = TIMER_SCHEDULED;
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask(mDeviceMAC);
        
        // Random delay [1,20[ seconds;
        Random r = new Random();
        int mDelay = (r.nextInt(5 - 1) + 1) * 1000;
        Log.i(WiFiDirect.TAG, "Wait " + mDelay + " to start connection.");
        timer.schedule(timerTask, mDelay); 
    }
    	 
    public void stopTimer() {
        //stop the timer, if it's not already null
        if (timer != null) {
        	Log.w(WiFiDirect.TAG, "Timer stopped");
            timer.cancel();
            TimerStatus = TIMER_IDLE;
            timer = null;
        }
    }

    public void initializeTimerTask(final String mDeviceMAC) {
        timerTask = new TimerTask() {
            public void run() {
                TimerStatus = TIMER_RUNNING;
                Log.i(WiFiDirect.TAG, "Starting WiFi P2P connection to " + mDeviceMAC);
                connectP2p(mDeviceMAC);
            }
        };
    }
    
    // NOTIFIERS ***************************************************************************
    
    /**
     * Notifies the WiFiDirect class that a new packet arrived.
     * @param mPacket Packet arrived 
     */
    private void notifyPacketReceived (List<Packet> mListOfPackets) {
    	for (WiFiDirectListener listener : mCallback.listeners) 
    	{
    	    listener.onPacketReceived(mListOfPackets);
    	}
    }
    
    /**
     * Notifies the WiFiDirect class that a new device was found.
     * @param mDevice Device found
     * @param mListOfSocialWeight 
     */
    private void notifyOnNewWiFiDirectDeviceFound (WiFiDirectDevice mDevice, Map<String, String> mListOfSocialWeight) {
    	for (WiFiDirectListener listener : mCallback.listeners) 
    	{
    	    listener.onNewWiFiDirectDeviceFound(mDevice, mListOfSocialWeight);
    	}
    }
    
    /**
     * Notifies the WiFiDirect class that a device disappeared.
     * @param mDevice Device found
     * @param mListOfSocialWeight 
     */
    private void notifyOnWiFiDirectDeviceDisappear (WiFiDirectDevice mDevice) {
    	for (WiFiDirectListener listener : mCallback.listeners) 
    	{
    	    listener.onWiFiDirectDeviceDisappear(mDevice);
    	}
    }
    
    private void notifyError(int mError) {
    	/*mError
    	 * 0 - Wi-Fi Disabled
    	 * 1 - Wi-Fi Enabled
    	 */
    	for (WiFiDirectListener listener : mCallback.listeners) 
    	{
    	    listener.error(mError);
    	}
    }

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
        case MESSAGE_READ:
        	
        	Bundle mPackets = msg.getData();
        	List<Packet> mListPackets = (List<Packet>) mPackets.getSerializable("data");
        	
            Log.d(WiFiDirect.TAG, "List of Packets received");
            disconnectP2p();
            notifyPacketReceived(mListPackets);
            break;

        case SOCKETERROR:
        	Log.e(WiFiDirect.TAG, "Socket Error. Disconnecting WiFi p2p");
        	for (WiFiDirectDevice mDevice : mAvailableDevices) {
				if (mDevice.getDevice().deviceAddress.equalsIgnoreCase(mSendingToDevice)) {
					Log.i(WiFiDirect.TAG, mSendingToDevice + " removed from available device.");
					mAvailableDevices.remove(mDevice);
					break;
				}
			}
        	disconnectP2p();
        	break;
		case EMPTY:
			Log.e(WiFiDirect.TAG, "No data received. Disconnecting WiFi p2p");
			disconnectP2p();
			break;
		}
		
		return true;
	}

}
