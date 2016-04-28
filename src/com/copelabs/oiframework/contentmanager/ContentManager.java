package com.copelabs.oiframework.contentmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.copelabs.oiaidllibrary.IRemoteOiFramework;
import com.copelabs.oiaidllibrary.IRemoteOiFrameworkCallback;
import com.copelabs.oiaidllibrary.UserDevice;
import com.copelabs.oiframework.router.Routing;
import com.copelabs.oiframework.router.RoutingListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;


public class ContentManager extends Service{
	
	/**
	 *  Follow Field Naming Conventions
 	 *
	 *	Non-public, non-static field names start with m.
	 *	Static field names start with s.
	 * 	Other fields start with a lower case letter. m stands for member variable or data member.
	 *	Public static final fields (constants) are ALL_CAPS_WITH_UNDERSCORES.
	 */

	private static final String TAG = "OiService";
	private static final String TAG2 = "Oi Demo";
	
	/* Framework Modules */
	private Routing pRouting;
	
	private int interfaceEnabled = 1;
	
	final RemoteCallbackList <IRemoteOiFrameworkCallback> mCallbacks = new RemoteCallbackList <IRemoteOiFrameworkCallback> ();
	
	@Override
	public void onCreate(){
		super.onCreate();
		//starts the timer that check the messages TTL
		initTTL_Timer();
		
		//initialize the modules needed by the CM.
		initializeModules();
	}

	@Override
	public void onDestroy(){
		if (mCallbacks != null)
			mCallbacks.kill();
		if (pRouting != null)
			pRouting.stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	/***********************
	/**
	 * All modules used inside the framework must be initialized inside this function.
	 * Each module are located in different packages.
	 */
	private void initializeModules(){
		pRouting = new Routing(this);
		pRouting.setRoutingListener(new RoutingListener() {

			@Override
			public void onPacketReceived(List<Packet> mListOfPackets) {
				receivePacket(mListOfPackets);
			}

			@Override
			public Map<String,List<Packet>> getListOfPackets(List<UserDevice> mListOfUserDevices) {
				List<Packet> mListAllPackets = FileIO.readFile(FileIO.TOSEND);
				if (mListAllPackets == null) 
					return null;
					
				Map<String, List<Packet>> mListOfPackets = new HashMap<String, List<Packet>>();
				for (UserDevice mDevice : mListOfUserDevices) {
					List <Packet> mPacketAvailableToDevice = new ArrayList<Packet>(); 
					for(Packet mPacket : mListAllPackets) {
						if ((mPacket.getIdDestination().toLowerCase()).contains(mDevice.getDevAdd().toLowerCase())) { 
							mPacketAvailableToDevice.add(mPacket);
						}
					}
					if (!mPacketAvailableToDevice.isEmpty()){
						mListOfPackets.put(mPacketAvailableToDevice.get(0).getIdDestination(), mPacketAvailableToDevice);
					}
				}
				return mListOfPackets;
			}

			@Override
			public void newDeviceFound(UserDevice mDevice) {
				newDeviceFoundToApp(mDevice);
			}
			
			@Override
			public void contactListUpdated(List<UserDevice> mListOfPackets) {
				contactListUpdatedToApp(mListOfPackets);
			}

			@Override
			public void deviceLost(UserDevice mDevice) {
				deviceLostToApp(mDevice);
			}

			@Override
			public void error(int mError) {
				sendError(mError);
			}
			
		});
	}
	
	private void initTTL_Timer() {
		Timer timer = new Timer();
        TimerTask mCleanTask = new CleanTask();
        int time = 60 * 1000 * 30; // 30 minutes
        timer.scheduleAtFixedRate(mCleanTask, time, time);
	}

	private final IRemoteOiFramework.Stub mBinder = new IRemoteOiFramework.Stub() {
		
		private String appName;

		@Override
		public void send(UserDevice mDest, final String message) throws RemoteException {
			
			if (pRouting == null)
				return;
			Packet mPacket = new Packet();
			mPacket.setAttributes(pRouting.getLocalMacAddress(), pRouting.getLocalName(), mDest.getDevAdd(), mDest.getDevName(), appName, message, System.currentTimeMillis());
			
			//It writes the new packet to the file
			if (FileIO.writeFile(FileIO.TOSEND, mPacket))
				Log.i(TAG, "Message to " + mDest.getDevName() + " saved in " + FileIO.TOSEND + " file.");
				
			
			//Notifies Routing about the new packet available.
			pRouting.newContentAvailable(mPacket);
		}
		
		@Override
		public void registerCallback(IRemoteOiFrameworkCallback callback, String appName)
                throws RemoteException {
            if(callback != null){
                mCallbacks.register(callback, appName);
                this.appName = appName;
            }
        }

		@Override
		public void unregisterCallback(IRemoteOiFrameworkCallback callback)
				throws RemoteException {
			if (callback != null) mCallbacks.unregister(callback);
		}

		@Override
		public List<UserDevice> getContactList() throws RemoteException {
			if(pRouting == null)
				return null;
			return pRouting.getContactList();
		}

		@Override
		public String getLocalName() throws RemoteException {
			if(pRouting == null)
				return null;
			return pRouting.getLocalName();
		}

		@Override
		public int isInterfaceEnabled() throws RemoteException {
			return interfaceEnabled;
		}
    };
    
    /**
     * Method the tries to send the packet to the correct app.
     * @param mPacket 
     * @return true if packet was successfully sent, false if app is not available (callback not registered)
     */
    
    private boolean sendPacketToApp(Packet mPacket) {
    	boolean mPacketSentToApp = false;
    	
    	if(mCallbacks.getRegisteredCallbackCount() == 0)
    		return false;
    	int i = mCallbacks.beginBroadcast();
		while (i > 0) {
			i--;
		    try {
		    	if (mCallbacks.getBroadcastCookie(i).equals(mPacket.getApplication())) {
		    		Log.i(TAG, "Packet sent to " + mPacket.getApplication());
		    		UserDevice infoSource = new UserDevice(mPacket.getIdSource(), mPacket.getNameSource());
		    		mCallbacks.getBroadcastItem(i).receive(infoSource, mPacket.getMessage());
		    		mPacketSentToApp = true;
		    	}
		    } catch (RemoteException e) {
		    	Log.e(TAG, "Error when calling callback of app " + mPacket.getApplication());
		    }
		}
		mCallbacks.finishBroadcast();
		return mPacketSentToApp;
    }
    
    /**
     * Method used by the others packages, when a new packet arrived.
     * It checks if the packet is for this device. If it is, tries to send to the correct app. 
     * If not saves the packet to a file.
     * @param mPacket
     */
    private void receivePacket(List<Packet> mListOfPackets) {
    	for(Packet mPacket : mListOfPackets) {
	    	//Check if packet is for this device
	    	if (mPacket.getIdDestination().equalsIgnoreCase(pRouting.getLocalMacAddress())) {
	    		//It tries to deliver the packet to the correct app
	    		if(!isPacketDuplicated(mPacket, FileIO.LOCALCACHE)) {
	    			sendPacketToApp(mPacket);
	    			//The app is not available at this moment. Save the packet to a local cache file.
	    			FileIO.writeFile(FileIO.LOCALCACHE, mPacket);
	    		} else {
	    			Log.w(TAG, "Packet from " + mPacket.getNameSource() + " already exist in local cache. Packet ignored.");
	    		}
	    	} else {
	    		if(!isPacketDuplicated(mPacket, FileIO.TOSEND)) {
	    			if (FileIO.writeFile(FileIO.TOSEND, mPacket)){
						Log.i(TAG, "Message saved to " + FileIO.TOSEND + " file.");
						Log.i(TAG2, "Message from " + mPacket.getNameSource() + " to " + mPacket.getNameDestination() + " saved for later dissemination.");
	    			}
	    		} else {
	    			Log.w(TAG, "Packet from " + mPacket.getNameSource() + " already exist in TO SEND cache. Packet ignored.");
	    			Log.i(TAG2, "Packet from " + mPacket.getNameSource() + " to " + mPacket.getNameDestination() + " already received. Packet ignored.");
	    		}
	    	}
    	}
    }
    /**
     * Check if the new packet received is already saved inside the Local Cache file
     * @param mNewPacket Packet to check if it this duplicated
     * @return true if is already available at local cache or false if not.
     */
    private boolean isPacketDuplicated(Packet mNewPacket, String mFile ) {
    	List<Packet> mListPacketsSaved = FileIO.readFile(mFile);
    	if (mListPacketsSaved == null)
    		return false;
    	for (Packet mSavedPacket : mListPacketsSaved) {
			if (mSavedPacket.getTimestamp() == mNewPacket.getTimestamp()) {
				// Duplicated packet
				return true;
			}
    	}
    	return false;
    }
    
    private void newDeviceFoundToApp(UserDevice mDevice) {
    	
    	if(mCallbacks.getRegisteredCallbackCount() == 0)
    		return;
    	int i = mCallbacks.beginBroadcast();
		while (i > 0) {
			i--;
		    try {
		    	mCallbacks.getBroadcastItem(i).newDeviceFound(mDevice);
		    } catch (RemoteException e) {
		    	Log.e(TAG, "Error sending new device found to applications.");
		    }
		}
		mCallbacks.finishBroadcast();
    }
    
    private void deviceLostToApp(UserDevice mDevice) {
    	
    	if(mCallbacks.getRegisteredCallbackCount() == 0)
    		return;
    	int i = mCallbacks.beginBroadcast();
		while (i > 0) {
			i--;
		    try {
		    	mCallbacks.getBroadcastItem(i).deviceLost(mDevice);
		    } catch (RemoteException e) {
		    	Log.e(TAG, "Error sending new device found to applications.");
		    }
		}
		mCallbacks.finishBroadcast();
    }
    
    private void sendError(int mError) {
    	interfaceEnabled = mError;
    	if(mCallbacks.getRegisteredCallbackCount() == 0)
    		return;
    	int i = mCallbacks.beginBroadcast();
		while (i > 0) {
			i--;
		    try {
		    	mCallbacks.getBroadcastItem(i).error(mError);;
		    } catch (RemoteException e) {
		    	Log.e(TAG, "Error sending error found to applications.");
		    }
		}
		mCallbacks.finishBroadcast();
    }
    
    private void contactListUpdatedToApp(List<UserDevice> mList) {
    	
    	if(mCallbacks.getRegisteredCallbackCount() == 0)
    		return;
    	int i = mCallbacks.beginBroadcast();
		while (i > 0) {
			i--;
		    try {
		    	mCallbacks.getBroadcastItem(i).contactListUpdated(mList);
		    } catch (RemoteException e) {
		    	Log.e(TAG, "Error sending updated contact to applications.");
		    }
		}
		mCallbacks.finishBroadcast();
    }
    
    	
}
