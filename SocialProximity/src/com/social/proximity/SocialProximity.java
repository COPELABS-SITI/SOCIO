/**
 * SocialProximity contains the core functionalities for the computation of 
 * social proximity prior to storing the required information in the provided database.
 *
 * @author Waldir Moreira (ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class SocialProximity {
    public static final String DATABASE_CHANGE = "com.social.proximity.CHANGE";
    private final String TAG = "Bluetooth Testing";
	private BTManager myBTManager;
	private ServiceBTListener btListener;
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	private OnNewHourUpdate newHour;
	private Context context;
	private DatabaseFunctions datasource;

    /**
     * SocialProximity constructor.
     * @param context The context which provides access to resources and specific classes of the application.
     * @param datasource The object that allows the handling of tables for social proximity computation.
     */
	public SocialProximity(Context context, DatabaseFunctions datasource) {
		this.context = context;
		this.datasource = datasource;
		myBTManager = new BTManager(context);
		myBTManager.startPeriodicScanning();
		btListener = new ServiceBTListener();
		myBTManager.setOnBTChangeListener(btListener);
		newHour = new OnNewHourUpdate(datasource);
		context.registerReceiver(newHour, new IntentFilter(com.social.proximity.OnNewHourUpdate.NEW_HOUR));
		
		Calendar calendar = Calendar.getInstance();
		
		// Change to hours
		calendar.add(Calendar.HOUR, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Intent intent = new Intent(com.social.proximity.OnNewHourUpdate.NEW_HOUR);
		alarmIntent = PendingIntent.getBroadcast(context, 345335, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600000, alarmIntent);
		
	}
	/**
     * Checks whether the device is in DB.
     * @param deviceAdd The device MAC address.
     * @return true if device is in DB, false otherwise
     */
	public boolean isDeviceOnDB(String deviceAdd){
		return datasource.hasBTDevice(deviceAdd);
	}
	   
	/**
     * Notifies a database change to the listeners.
     */
	private void notifyDataBaseChange () {
    	Intent i = new Intent(SocialWeight.socialWeight_key);
    	context.sendBroadcast(i);
	}
	
	/**
	 * Provides a list of encountered peers and the social weights towards them.
	 * @return listWeights List of peers with weights sorted in a descending way.
	 */
	public ArrayList<SocialWeight> getSocialWeightDetails(){
		
		Log.i(TAG,"*****getSocialWeightDetails");
		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice();
		
		int currentTimeSlot = getTimeSlot();
		ArrayList<SocialWeight> listWeights = new ArrayList<SocialWeight> ();

		Iterator<String> devHighestWeight = tempListOfDevice.keySet().iterator();
		
		//Getting highest social weight value
		double highestSocialWeight = 0.0;
		while (devHighestWeight.hasNext()){
			String btDev = devHighestWeight.next();
			
			if(datasource.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot) > highestSocialWeight)
				highestSocialWeight = datasource.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot);
		}
		
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
		
		while (devIterator.hasNext()){
			SocialWeight table = new SocialWeight();
			String btDev = devIterator.next();
			table.setMacAddress(datasource.getBTDevice(btDev).getDevAdd());
			table.setDeviceName(datasource.getBTDevice(btDev).getDevName());
			table.setSocialWeight( (int) ((datasource.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot)*5)/highestSocialWeight));
			listWeights.add(table);
		}
		
			Collections.sort(listWeights, new CustomComparator());
			Log.i(TAG,"*****listWeights: "+ listWeights);
			
			
			return listWeights;
	}
	
	public class CustomComparator implements Comparator<SocialWeight> {
		int currentTimeSlot = getTimeSlot();
		@Override
		public int compare(SocialWeight entry1, SocialWeight entry2) {
			if (entry1.getSocialWeight() >= entry2.getSocialWeight()) {
	            return -1;
	        } else {
	            return 1;
	        }
		}
		
	}
	
	/**
	 * Stops Bluetooth-related tasks (i.e., scanning, unregister broadcast receivers)
	 * @param context The context which provides access to resources and specific classes of the application
	 */
    public void close(Context context) {
    	myBTManager.close(context);
    	context.unregisterReceiver(newHour);
    }
	
	/**
    * Provides the current time slot 
    * @return currentTimeSlot The actual time slot
    */
	public int getTimeSlot(){
		int currentTimeSlot = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		Log.i(TAG,"Current hour(slot): " + currentTimeSlot);
		//writeToSD("Current hour(slot): " + currentTimeSlot);
		
		return currentTimeSlot;
	}
    
    /**
     * Writes on a file for Bluetooth debugging
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

    /**
     * ServiceBTListener class listens for found Bluetooth
     * devices, checks whether they are already in the database.
     * If not in the database, entries are created to the respective device.
     */
    class ServiceBTListener implements BTDeviceFinder {

		public void onDeviceFound(BluetoothDevice device, BluetoothClass btClass) {
    		long currentTime = System.nanoTime(); 
        	 
			Log.i(TAG,"Device found @ " + currentTime );
			
			//writeToSD("Device found @ " + currentTime );
//			Log.i(TAG,"Device encountered at: " + DateFormat.format("hh:mm:ss", System.currentTimeMillis()));
			

//			if (btClass.getDeviceClass() == 524 || btClass.getDeviceClass() == 268){
    		if (filterDevice(device, btClass)){
//				Log.i(TAG,"Device must be added to DB");
				if(!isDeviceOnDB(device.getAddress())){
					
//					Log.i(TAG,"Device name: " + device.getName() + " not in the database");
					Log.i(TAG,"Adding " + device.getName() + " to database");
					
					//writeToSD("Adding " + device.getName() + " to database");
							
					BTUserDevice btDev = new BTUserDevice();
					BTUserDevEncounterDuration duration = new BTUserDevEncounterDuration();
					BTUserDevAverageEncounterDuration averageDuration = new BTUserDevAverageEncounterDuration();
					BTUserDevSocialWeight socialWeight = new BTUserDevSocialWeight();
					
					btDev.setDevAdd(device.getAddress());
					btDev.setDevName(device.getName());
					btDev.setEncounterTime(currentTime);
					
					duration.setDevAdd(device.getAddress());
					averageDuration.setDevAdd(device.getAddress());
					socialWeight.setDevAdd(device.getAddress());
					for(int timeSlot = 0; timeSlot < 24; timeSlot++){
						duration.setEncounterDuration(timeSlot, 0.0);
						averageDuration.setAverageEncounterDuration(timeSlot, 0.0);
						socialWeight.setSocialWeight(timeSlot, 0.0);
					}
					
					datasource.registerNewBTDevice(btDev, duration, averageDuration, socialWeight);
					Log.i(TAG,"Device name: " + device.getName() + " now added in the database");
					Log.i(TAG,"**************************");
					
					//writeToSD("Device name: " + device.getName() + " now added in the database");
					//writeToSD("**************************");
				}else{
//					Log.i(TAG,"*************Data from DB*************");
//					Log.i(TAG,"Total number of neighbors: " + datasource.getAllBTDevice().entrySet().size() + " in the database");
//					Log.i(TAG,"Device name: " + (datasource.getBTDevice(device.getAddress())).getDevName() + " in the database");
//					Log.i(TAG,"Device MAC: " + (datasource.getBTDevice(device.getAddress())).getDevAdd());
//					Log.i(TAG,"Device encountered at: " + DateFormat.format("hh:mm:ss", (datasource.getBTDevice(device.getAddress())).getEncounterTime()));
//					Log.i(TAG,"Device encountered at: " + (datasource.getBTDevice(device.getAddress())).getEncounterTime());
	
					BTUserDevice btDev = datasource.getBTDevice(device.getAddress());
					BTUserDevEncounterDuration duration = datasource.getBTDeviceEncounterDuration(device.getAddress());
					
					Log.i(TAG,"Updating encounter duration for: " + btDev.getDevName() 
							+ " - MAC: " + btDev.getDevAdd());
					
					//writeToSD("Updating encounter duration for: " + btDev.getDevName() + " - MAC: " + btDev.getDevAdd());
					

//					BTUserDevAverageEncounterDuration averageDuration = datasource.getBTDeviceAverageEncounterDuration(device.getAddress()) ;
//					BTUserDevSocialWeight socialWeight = datasource.getBTDeviceSocialWeight(device.getAddress());
					
//					long timeInVicinity = currentTime;
					
//					Log.i(TAG,"Time now: " + DateFormat.format("hh:mm:ss", System.currentTimeMillis()));
//					Log.i(TAG,"Nano time now: " + System.nanoTime());
//					Log.i(TAG,"Encounter time: " + btDev.getEncounterTime());
//					Log.i(TAG,"Previous TimeInVicinity: " + btDev.getTimeInVicinity());
//					Log.i(TAG,"TimeInVicinity now: " + timeInVicinity);
//					btDev.setTimeInVicinity(btDev.getTimeInVicinity() + timeInVicinity-btDev.getEncounterTime());
					
					long encounterEnd = currentTime;

					int currentTimeSlot = getTimeSlot();
					
					double newEncounterDuration = (encounterEnd-btDev.getEncounterStart())/1000000000.0;
					
					//writeToSD("newEncounterDuration - BTCore: " + newEncounterDuration);
					
					duration.setEncounterDuration(currentTimeSlot, newEncounterDuration);
					
					datasource.updateBTDeviceAndDuration(btDev, duration);
					
//					Log.i(TAG,"newEncounterDuration from DB: " + duration.getEncounterDuration(currentTimeSlot));
					
					notifyDataBaseChange ();
					
					showDevicesOnDB();
//					Log.i(TAG,"Device time in vicinity: " + (double)datasource.getBTDevice(device.getAddress()).getTimeInVicinity()/1000000000);
				}
			}//else
				//Log.i(TAG,"Device of no interest");
		}
		
		 /**
	     * Show the bluetooth devices stored on the DB.
	     */
		public void showDevicesOnDB() {
			int numberDevOnDB = datasource.getAllBTDevice().entrySet().size();
			
			Log.i(TAG,"*************Data from DB*************");
			Log.i(TAG,"Total number of neighbors: " + numberDevOnDB + " in the database");
			
			//writeToSD("*************Data from DB*************");
			//writeToSD("Total number of neighbors: " + numberDevOnDB + " in the database");
					
			Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 
			
//			Set<String> deviceSet = tempListOfDevice.keySet();
			
			Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
			
			while (devIterator.hasNext()){
				String btDev = devIterator.next();
//				Log.i(TAG,"String btDev: " + btDev);
				Log.i(TAG,"Device name: " + (datasource.getBTDevice(btDev)).getDevName() + " in the database");
				Log.i(TAG,"Device MAC: " + btDev);
				
				//writeToSD("Device name: " + (datasource.getBTDevice(btDev)).getDevName() + " in the database");
				//writeToSD("Device MAC: " + btDev);
								
				int timeSlot = 0;
//				while(devEncDurationIterator.hasNext()){
//				Log.i(TAG,"Device encounter duration: ");
				while(timeSlot<24){	
//					String btDevEncDuration = devEncDurationIterator.next();
					
						if((datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot)!=0.0){
							Log.i(TAG,"Encounter duration - Time Slot" + timeSlot + ": " + 
								(datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot));
//						Log.i(TAG,"Encounter duration - Time Slot" + timeSlot + ": " + 
//								(double)(datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot)/1000000000);
						Log.i(TAG,"Avg encounter duration - Time Slot" + timeSlot + ": " + 
								String.valueOf((datasource.getBTDeviceAverageEncounterDuration(btDev)).getAverageEncounterDuration(timeSlot)));
						Log.i(TAG,"Social weight - Time Slot" + timeSlot + ": " + 
								String.valueOf((datasource.getBTDeviceSocialWeight(btDev)).getSocialWeight(timeSlot)));
						
						//writeToSD("Encounter duration - Time Slot" + timeSlot + ": " + (datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot));
						//writeToSD("Avg encounter duration - Time Slot" + timeSlot + ": " +  (datasource.getBTDeviceAverageEncounterDuration(btDev)).getAverageEncounterDuration(timeSlot));
						//writeToSD("Social weight - Time Slot" + timeSlot + ": " + (datasource.getBTDeviceSocialWeight(btDev)).getSocialWeight(timeSlot));
					}					
					timeSlot++;
//					Log.i(TAG,"Time Slot++" + timeSlot);
				}
				Log.i(TAG,"---------------------------------");
				//writeToSD("---------------------------------");
			}
			Log.i(TAG,"************************************");
			//writeToSD("************************************");
		}
		
	    /**
	     * Checks whether the device is of interest.
	     * Bluetooth devices can be of different types but only smart phones and mobile PCs are considered.
	     * @param btClass The device class.
	     * @return true if device is of interest, false otherwise.
	     */
		public boolean filterDevice(BluetoothDevice device, BluetoothClass btClass){
			String devtype;
    		
    		switch (btClass.getDeviceClass()) {
    		case 1076: devtype = "AUDIO VIDEO CAMCORDER"; break;
        	case 1056: devtype = "AUDIO VIDEO CAR AUDIO"; break;
        	case 1032: devtype = "AUDIO VIDEO HANDSFREe"; break;
        	case 1048: devtype = "AUDIO VIDEO HEADPHONES"; break;
        	case 1064: devtype = "AUDIO VIDEO HIFI AUDIO"; break;
        	case 1044: devtype = "AUDIO VIDEO LOUDSPEAKER"; break;
        	case 1040: devtype = "AUDIO VIDEO MICROPHONe"; break;
        	case 1052: devtype = "AUDIO VIDEO PORTABLE AUDIO"; break;
        	case 1060: devtype = "AUDIO VIDEO SET TOP BOX"; break;
        	case 1024: devtype = "AUDIO VIDEO UNCATEGORIZED"; break;
        	case 1068: devtype = "AUDIO VIDEO VCR"; break;
        	case 1072: devtype = "AUDIO VIDEO VIDEO CAMERA"; break;
        	case 1088: devtype = "AUDIO VIDEO VIDEO CONFERENCING"; break;
        	case 1084: devtype = "AUDIO VIDEO VIDEO DISPLAY AND LOUDSPEAKER"; break;
        	case 1096: devtype = "AUDIO VIDEO VIDEO GAMING TOY"; break;
        	case 1080: devtype = "AUDIO VIDEO VIDEO MONITOR"; break;
        	case 1028: devtype = "AUDIO VIDEO WEARABLE HEADSET"; break;
        	case 260: devtype = "COMPUTER DESKTOP"; break;
        	case 272: devtype = "COMPUTER HANDHELD PC PDA"; break;
        	case 268: devtype = "COMPUTER LAPTOP"; break;
        	case 276: devtype = "COMPUTER PALM SIZE PC PDA"; break;
        	case 264: devtype = "COMPUTER SERVER"; break;
        	case 256: devtype = "COMPUTER UNCATEGORIZED"; break;
        	case 280: devtype = "COMPUTER WEARABLe"; break;
        	case 2308: devtype = "HEALTH BLOOD PRESSURe"; break;
        	case 2332: devtype = "HEALTH DATA DISPLAY"; break;
        	case 2320: devtype = "HEALTH GLUCOSe"; break;
        	case 2324: devtype = "HEALTH PULSE OXIMETER"; break;
        	case 2328: devtype = "HEALTH PULSE RATe"; break;
        	case 2312: devtype = "HEALTH THERMOMETER"; break;
        	case 2304: devtype = "HEALTH UNCATEGORIZED"; break;
        	case 2316: devtype = "HEALTH WEIGHING"; break;
        	case 516: devtype = "PHONE CELLULAR"; break;
        	case 520: devtype = "PHONE CORDLESS"; break;
        	case 532: devtype = "PHONE ISDN"; break;
        	case 528: devtype = "PHONE MODEM OR GATEWAY"; break;
        	case 524: devtype = "PHONE SMART"; break;
        	case 512: devtype = "PHONE UNCATEGORIZED"; break;
        	case 2064: devtype = "TOY CONTROLLER"; break;
        	case 2060: devtype = "TOY DOLL ACTION FIGURe"; break;
        	case 2068: devtype = "TOY GAMe"; break;
        	case 2052: devtype = "TOY ROBOT"; break;
        	case 2048: devtype = "TOY UNCATEGORIZED"; break;
        	case 2056: devtype = "TOY VEHICLe"; break;
        	case 1812: devtype = "WEARABLE GLASSES"; break;
        	case 1808: devtype = "WEARABLE HELMET"; break;
        	case 1804: devtype = "WEARABLE JACKET"; break;
        	case 1800: devtype = "WEARABLE PAGER"; break;
        	case 1792: devtype = "WEARABLE UNCATEGORIZED"; break;
        	case 1796: devtype = "WEARABLE WRIST WATCH"; break;
        	default: devtype="Other type of device"; break;
            }
			
    		Log.i(TAG,"Device name: " + device.getName() + " - MAC: " + device.getAddress() + " - Type: " + devtype);
    		//writeToSD("Device name: " + device.getName() + " - MAC: " + device.getAddress() + " - Type: " + devtype);
			
    		if (btClass.getDeviceClass() == 524 || btClass.getDeviceClass() == 268){
//    			Log.i(TAG,"Device must be added to DB");
    			return true;
    		}
    		Log.i(TAG,"Device of no interest");
    		//writeToSD("Device of no interest");
			return false;
		}
	}
}
