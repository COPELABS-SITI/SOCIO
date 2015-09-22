/**
 * BTManager provides some methods to provide extended
 * functionality to the android BluetoothAdapter.
 *
 * @author Waldir Moreira (COPELABS/ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class BTManager {
	public static int DISCOVER_INTERVAL = 20000; // check how long BT scan is !!!!!!
	
    private static final String TAG = "BTManager";
    private Context mContext;
	
	private BluetoothAdapter androidBTAdapter;
	private FindBluetoothDevice BTDevFinder;
	private adapterBroadcastReceiver adapterReceiver;
	private BTDeviceFinder listener;
	
	public boolean isScanningActive = false;
	public boolean isWaitingScanResults = false;
	private static boolean mBTisTurningOnOff = false;
	
	private Handler mHandler = new Handler();
	
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
					if (startDiscovery()) {
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
	 * BTManager constructor.
	 * @param c The context which provides access to resources and specific classes of the application.
	 */
	public BTManager(Context c) {
		androidBTAdapter = BluetoothAdapter.getDefaultAdapter();
		BTDevFinder = new FindBluetoothDevice();
		adapterReceiver = new adapterBroadcastReceiver();
		c.registerReceiver(BTDevFinder, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		c.registerReceiver(adapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		c.registerReceiver(adapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		mContext = c;
	}
	
	/**
     * FindBluetoothDevice class receives an event (i.e., ACTION_FOUND) 
     * when a Bluetooth-enabled device is found.
	 */
	class FindBluetoothDevice extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		             // Get the BluetoothDevice object from the Intent
		        	 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        	 
		        	 // Get the BluetoothClass object from the Intent
		        	 BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
		        
		        	 // BluetoothDevice and BluetoothClass objects to listener to be handled in the BluetoothCore
//		        	 Log.i("BT","I am in BTManager");
		        	 listener.onDeviceFound(device, btClass);
		             
		        }
		    }
		};
		
		/**
	     * adapterBroadcastReceiver class receives events  
	     * concerning the Bluetooth adapter (e.g., discovery process is finished,
	     * adapter state has changed).
		 */
		class adapterBroadcastReceiver extends BroadcastReceiver {
			@Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();

	            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
	            {
	                Log.v("BT","Entered the Finished ");
	                //writeToSD("Entered the Finished ");
	                isWaitingScanResults = false;
	                return;
	            }
	            
	            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	            	 Log.v("BT","Bluetooth enabled");
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
							Log.w(TAG, "BT turning off");
							break;
						case BluetoothAdapter.STATE_ON:
							mBTisTurningOnOff = false;
							startPeriodicScanning();
							break;
						case BluetoothAdapter.STATE_TURNING_ON:
							Toast.makeText(mContext, "Enabling Bluetooth for Social Pipeline.", Toast.LENGTH_SHORT).show();
							mBTisTurningOnOff = true;
							Log.w(TAG, "BT turning on");
							break;
					 }
	            }
	            	
	        }
		};
		
		/**
		 * Stops Bluetooth-related tasks (i.e., scanning, unregister broadcast receivers).
		 * @param c The context which provides access to resources and specific classes of the application.
		 */
		public void close (Context c) {
			this.stopPeriodicScanning();
			c.unregisterReceiver(BTDevFinder);
			c.unregisterReceiver(adapterReceiver);
		}

		/**
		 * Starts periodic scanning for Bluetooth-enabled devices.
		 */
		public void startPeriodicScanning() {
//			 Log.i("BT", "Periodic Discovery Started");
			isScanningActive = true;

			mHandler.removeCallbacks(runScan);
//			Log.i("BT", "removeCallBack");
			mHandler.post(runScan);
			
		}
			
		/**
		 * Stops periodic scanning for Bluetooth-enabled devices.
		 */
		private void stopPeriodicScanning() {
			isScanningActive = false;
			mHandler.removeCallbacks(runScan);			
		}
	
		/**
		 * Checks whether Bluetooth is enabled.
		 * @return true if Bluetooth is enabled.
		 */
		public boolean isBTEnabled () {
			return androidBTAdapter.isEnabled();
		}	
		
		/**
		 * Enables Bluetooth.
		 * @return true if Bluetooth is successfully enabled.
		 */
		public boolean enableBT() {
			if (!mBTisTurningOnOff)
				return androidBTAdapter.enable();
			else 
				return true;
		}
		
		/**
		 * Starts the discovery process.
		 * @return true if process successfully started.
		 */
		public boolean startDiscovery() {
			Log.v("BT","Scan started");
			//writeToSD("Scan started");
			return androidBTAdapter.startDiscovery();
		}
		
		/**
		 * Sets the listener for Bluetooth-related actions.
		 * @param listener The listener for the action.
		 */
	    public void setOnBTChangeListener (BTDeviceFinder listener) {
	        this.listener = listener;
	    }
	    
	    /**
	     * Clear the listener for Bluetooth-related actions.
	     */
	    public void clearOnBTChangeListener () {
	        this.listener = null;
	    }
	    
        /**
         * Writes on a file for Bluetooth debugging.
         */
	    private void writeToSD(String text){
	    	File file = new File(Environment.getExternalStorageDirectory()+File.separator+"BT","BTOutput.txt");
	    	String currentTime = (String) DateFormat.format("dd/MM - HH:mm:ss.sss", Calendar.getInstance().getTime());
	    	try {
				FileWriter writer = new FileWriter(file, true);
				String line = currentTime + " " + text + "\n";
				writer.write(line); 
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
}
