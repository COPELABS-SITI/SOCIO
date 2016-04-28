package com.copelabs.oiframework.bt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.copelabs.oiframework.socialproximity.SocialProximity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application. 
 * This class provides some methods to provide extended
 * functionality to the android BluetoothAdapter.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

public class BluetoothManager {
	public static int DISCOVER_INTERVAL = 180000;
	
	 // for debugging purposes
	private boolean debug = true;
    private static final String TAG = "BTManager";
    private Context mContext;
	
	private BluetoothAdapter androidBTAdapter;
	
	// Used to identify Oi! devices
	private BluetoothServerSocket socket;
	private UUID uuid = UUID.fromString("01010101-0101-0101-0101-010101010101");
	private Map<BluetoothDevice, BluetoothClass> btDeviceList = new HashMap<BluetoothDevice, BluetoothClass>();
	
	private FindBluetoothDevice BTDevFinder;
	private adapterBroadcastReceiver adapterReceiver;
	private BTDeviceFinder listener;
	
	public boolean isScanningActive = false;
	public boolean isWaitingScanResults = false;
	private static boolean mBTisTurningOnOff = false;
	
	private String fetchingUUIDfromDevice;
	
	Iterator<BluetoothDevice> BTDeviceIterator;
	
	private Handler mHandler = new Handler();
	
	/**
	* This method allows for starting and stopping periodic Bluetooth scanning.
	**/
	private Runnable runScan = new Runnable() {
		public void run() {
			if (isScanningActive) {
				if (!isBTEnabled()) {
					if(enableBT()) {
						stopPeriodicScanning();
					} else {
						Toast.makeText(mContext, "Error enabling BT. Closing Social Pipeline.", Toast.LENGTH_SHORT).show();
						close(mContext);
					}
					return;
				}
				if (!isWaitingScanResults && isBTEnabled()) {
					btDeviceList.clear();
					if (startDiscovery()) {
						if(debug){
							Log.i(TAG, "Starting BT scaning...");
//							writeToSD("Starting BT scaning...");
						}
						isWaitingScanResults = true;
					} 
					mHandler.postDelayed(runScan, DISCOVER_INTERVAL);
				}
				else if (isWaitingScanResults) {
	        		mHandler.postDelayed(runScan, DISCOVER_INTERVAL);
	        		isWaitingScanResults = false;
				}
			}
		}
	};
	
	/**
	* This method is the constructor for BluetoothManager.
	* @param c The context.
	**/
	public BluetoothManager(Context c) {
		androidBTAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//Advertised UUID to let peer node knows that current node runs Oi!  
		try {
			socket = androidBTAdapter.listenUsingInsecureRfcommWithServiceRecord("oi", uuid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BTDevFinder = new FindBluetoothDevice();
		adapterReceiver = new adapterBroadcastReceiver();
		c.registerReceiver(BTDevFinder, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		c.registerReceiver(BTDevFinder, new IntentFilter(BluetoothDevice.ACTION_UUID));
		c.registerReceiver(adapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		c.registerReceiver(adapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		mContext = c;
	}

	/**
	* This class allows for getting information
	* from devices found during scan.
	**/
	class FindBluetoothDevice extends BroadcastReceiver {
		/**
		* This method receives different BluetoothDevice actions.
		* @param context The context
		* @param intent The intent
		**/
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
		        
             // Get the BluetoothDevice object from the Intent
        	 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        	 // Get the BluetoothClass object from the Intent
        	 BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
	        
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//		        	Log.i(TAG,"I am in ACTION_FOUND");
	        
	        	//Add to btDeviceList for later filtering
	        	if(debug){
	        		Log.i(TAG,"Adding: " + device.getName() + " to btDeviceList !!");
//		        		Parcelable[] uuidExtra = device.getUuids();
//		        		Log.i(TAG,"List of received UUDIs: " + uuidExtra);
//		        		if(uuidExtra != null){
//		        		for (int i=0; i<uuidExtra.length; i++) {
//		        			Log.i(TAG,"\n Service: " + uuidExtra[i].toString());
//		        		 }
//		        		}
	        	}
	        	btDeviceList.put(device, btClass);
//		        	Log.i(TAG,"btDeviceList: " + btDeviceList.toString());
//		        	listener.onDeviceFound(device, btClass);
	        }
	       
	        // Get UUIDs from neighbor device
	        if(BluetoothDevice.ACTION_UUID.equals(action) ) {
	        	if (fetchingUUIDfromDevice == null)
	        		return;
	        	if (!fetchingUUIDfromDevice.equalsIgnoreCase(device.getAddress()))
	        		return;

//		       	Log.i(TAG,"I am in ACTION_UUID " + device.getName());
		        Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
		        	
	        	if(uuidExtra!=null){
	        		if(debug){
	        			Log.i(TAG,"looking for this uuid: " + uuid);
	        		}
	        		for (int i=0; i<uuidExtra.length; i++) {
	        		
	        			//Filter devices and report only on device with Oi! UUID
	        			if(debug){
	        				Log.i(TAG,"\n  Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());
		        		}
		        		
		        		if(uuidExtra[i].toString().equals(uuid.toString())){
		        			if(debug){
		        				Log.i(TAG,"\n  Device: " + device.getName() + " sent social proximity !!");
		        			}
		        			//Send this device to Social Proximity
		        			listener.onDeviceFound(device, btDeviceList.get(device));  
		        		}
		            }
	        	} else {
	        		Log.i(TAG,"UUID null for device " + device.getName());
	        	}
	        	if (BTDeviceIterator == null)
	        		return;
                if (BTDeviceIterator.hasNext()) {
                  // Get Services for paired devices
                  BluetoothDevice itrDev = BTDeviceIterator.next();
                  Log.i(TAG, "Fetching UUIDs for " + itrDev.getName());
                 
                  //Log.i(TAG, "Getting Services for " + device.getName() + ", " + device);
                  if(!itrDev.fetchUuidsWithSdp()) {
                	  Log.i(TAG, "\nSDP Failed for " + itrDev.getName());
                	  return;
                  }
                  fetchingUUIDfromDevice = itrDev.getAddress();
                } else {
                	fetchingUUIDfromDevice = null;
                }
	        } 
		}
	};
		
	/**
	* This class allows for getting information from BluetoothAdapter.
	**/
	class adapterBroadcastReceiver extends BroadcastReceiver {
		//private BTDeviceFinder listener;

		/**
		* This method receives different actions.
		* @param context The context
		* @param intent The intent
		**/
		@Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            	if(debug){
	                Log.i(TAG,"Entered the Finished ");
//		                writeToSD("Entered the Finished ");
            	}
                isWaitingScanResults = false;
                if(SocialProximity.appRestarted)
                	SocialProximity.appRestartReset();
                
                // When discovery process is done, acquire UUIDs of encountered peers
               // Iterator<BluetoothDevice> itr = btDeviceList.keySet().iterator();
                BTDeviceIterator = btDeviceList.keySet().iterator();
                if (BTDeviceIterator.hasNext()) {
                	// Get Services for paired devices
                	BluetoothDevice device = BTDeviceIterator.next();
                	Log.i(TAG, "Fetching UUIDs for  " + device.getName());
                	if(!device.fetchUuidsWithSdp()) {
                		Log.i(TAG, "\nSDP Failed for " + device.getName());
                		return;
                	}
                	fetchingUUIDfromDevice = device.getAddress();
                }
            }
            
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            	 //Log.v(TAG,"Bluetooth enabled");
            	 final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                         BluetoothAdapter.ERROR);
            	 switch (state) {
            	 	case BluetoothAdapter.STATE_OFF:
            	 		mBTisTurningOnOff = false;
            	 		//TODO: Check this. If the user disables BT, USENSE automatically enables it again.
            	 		enableBT();
            	 		break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						mBTisTurningOnOff = true;
						//Log.w(TAG, "BT turning off");
						break;
					case BluetoothAdapter.STATE_ON:
						mBTisTurningOnOff = false;
						startPeriodicScanning();
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						Toast.makeText(mContext, "Enabling Bluetooth for Social Pipeline.", Toast.LENGTH_SHORT).show();
						mBTisTurningOnOff = true;
						//Log.w(TAG, "BT turning on");
						break;
				 }
            }
        }
	};
		
	/**
	* This method stops scanning and unregister BroadcastReceivers.
	* @param context The context
	**/
	public void close (Context c) {
		this.stopPeriodicScanning();
		c.unregisterReceiver(BTDevFinder);
		c.unregisterReceiver(adapterReceiver);
	}

	/**
	* This method starts scanning.
	**/
	public void startPeriodicScanning() {
		isScanningActive = true;
		mHandler.removeCallbacks(runScan);
		mHandler.post(runScan);
	}
	
	/**
	* This method stops scanning.
	**/
	private void stopPeriodicScanning() {
		isScanningActive = false;
		mHandler.removeCallbacks(runScan);			
	}

	/**
	* This method checks whether Bluetooth is enabled.
	**/
	public boolean isBTEnabled () {
		return androidBTAdapter.isEnabled();
	}	
	
	/**
	* This method enables Bluetooth.
	**/
	public boolean enableBT() {
		if (!mBTisTurningOnOff)
			return androidBTAdapter.enable();
		else 
			return true;
	}
		
	/**
	* This method starts Bluetooth discovery process.
	**/
	public boolean startDiscovery() {
//			if(debug){
//				Log.v(TAG,"Scan started");
//				writeToSD("Scan started");
//			}
		return androidBTAdapter.startDiscovery();
	}
	
	/**
	* This method sets a change listener.
	* @param listener The BTDeviceFinder listener
	**/
    public void setOnBTChangeListener (BTDeviceFinder listener) {
        this.listener = listener;
    }
    
    /**
	* This method clears the change listener.
	**/
    public void clearOnBTChangeListener () {
        this.listener = null;
    }
    
    /**
     * This method provides the MAC Address of local Bluetooth adapter.
     * @return localMacAdd The local MAC Address
     */
    public List<String> getLocalInfo(){
    	List<String> localinfo = new ArrayList<String>();
    	localinfo.add(0, androidBTAdapter.getName()); 
    	localinfo.add(1, androidBTAdapter.getAddress());
		return localinfo;
    }
    
//        /**
//         * Writes on a file for Bluetooth debugging
//         */
//	    private void writeToSD(String text){
//	    	File file = new File(Environment.getExternalStorageDirectory()+File.separator+"SocialProximity","SocialProximityOutput.txt");
//	    	String currentTime = (String) DateFormat.format("dd/MM - hh:mm:ss.sss", Calendar.getInstance().getTime());
//	    	try {
//				FileWriter writer = new FileWriter(file, true);
//				String line = currentTime + " " + text + "\n";
//				writer.write(line); 
//				writer.flush();
//				writer.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    }
	    
}
