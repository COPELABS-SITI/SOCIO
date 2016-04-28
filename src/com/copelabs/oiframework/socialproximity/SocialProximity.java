package com.copelabs.oiframework.socialproximity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.copelabs.oiaidllibrary.UserDevice;
import com.copelabs.oiframework.bt.BTDeviceFinder;
import com.copelabs.oiframework.bt.BluetoothManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * This class is contains the core functionalities of the application. 
 * The BTManager provides all the information from Bluetooth adapter so this class can 
 * perform the social context analysis prior to storing the required information in the database.
 * @author Waldir Moreira (COPELABS/ULHT)
 */
public class SocialProximity {
	private final static String TAG = "Social Proximity";
	private BluetoothManager myBTManager;
	private ServiceBTListener btListener;
	
	// for debugging purposes
	private static boolean debug = true;
	
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	private OnSocialWeightUpdate newHour;
	
	public static boolean appRestarted = false;
		
	private Context context;
	private DataBase database;
	public ArrayList<SocialProximityListener> listeners = new ArrayList<SocialProximityListener> ();
	
    public static final String DATABASE_CHANGE = "com.social.proximity.CHANGE";

	/**
	* This method is the constructor for SocialProximity.
	* @param context The context.
	**/
	public SocialProximity(Context context){
		this.context = context;
		
		//Initializing Database, Bluetooth Manager and NewHourUpdate
		initializeModules(context); 
		
		//Set an hourly alarm that triggers social weight computation
		setNewHourAlarm();
		
		//Create preference file to understand when app restarted
		createPrefFile();
	}
	
	/**
	 * This method initializes all modules used by Social Proximity.
	 * Each module is located in different packages.
	 * @param context The context.
	 */
	private void initializeModules(Context context){
		database = new DataBase(context);
		database.openDB(true);
		myBTManager = new BluetoothManager(context);
		myBTManager.startPeriodicScanning();
		btListener = new ServiceBTListener();
		myBTManager.setOnBTChangeListener(btListener);
		newHour = new OnSocialWeightUpdate(database, this);
		
//        /**
//         * Creates a file for Bluetooth debugging
//         */
//		if(debug){
//	        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//	            //handle case of no SDCARD present
//	        } else {
//	            String dir = Environment.getExternalStorageDirectory()+File.separator+"SocialProximity";
//	            //create folder
//	            File folder = new File(dir); //folder name
//	            folder.mkdirs();
//	            Log.i(TAG,"!!!!!! ESCREVENDO ARQUIVO !!!!!! em " + dir);
//	            //create file
//	            File file = new File(dir, "SocialProximityOutput.txt");
//	            try {
//	            	if(!file.exists())
//	        			file.createNewFile();
//	            	else
//	            		file.delete();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	        }
//		} 
	}

	/**
	 * This method sets an alarm triggered every four minutes for social weight computation.
	 */
	private void setNewHourAlarm() {
		context.registerReceiver(newHour, new IntentFilter(com.copelabs.oiframework.socialproximity.OnSocialWeightUpdate.NEW_HOUR));
		Calendar calendar = Calendar.getInstance();
		
		//calendar.add(Calendar.HOUR, 1);**************** Uncomment for hour-based SW updates
		//calendar.set(Calendar.MINUTE, 0);**************** Uncomment for hour-based SW updates
		calendar.add(Calendar.MINUTE, 4); // 4-minute-based SW updates, Comment this for hour-based SW updates
		//calendar.set(Calendar.SECOND, 0);**************** Uncomment for hour-based SW updates
		
		Intent intent = new Intent(com.copelabs.oiframework.socialproximity.OnSocialWeightUpdate.NEW_HOUR);
		alarmIntent = PendingIntent.getBroadcast(context, 345335, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		//alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600000, alarmIntent);**************** Uncomment for hour-based SW updates
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 240000, alarmIntent); // 4-minute-based SW updates, Comment this for hour-based SW updates

	}
	
	/**
	 * This method resets flag for when app was restarted
	 */
	public static void appRestartReset(){
		appRestarted = false;
		if(debug){
			//writeToSD("appRestarted set to false");
		}
	}
	
	/**
	 * This method creates the preference file with information about the day and time slot when the app was started.
	 * If the file exists, updates it accordingly
	 */
	private void createPrefFile() {
		
		long timeStamp = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeStamp);
		String day = "day";
		String dayNumber = "dayNumber";
		String timeSlot = "timeSlot";
		
		int dayCounter = 1;
		int dNumber = c.get(Calendar.DAY_OF_YEAR);
		int slot = c.get(Calendar.HOUR_OF_DAY);
		
		long currentTime = System.nanoTime(); 
		
		SharedPreferences.Editor daySample = this.context.getSharedPreferences("DayTime", Context.MODE_PRIVATE).edit();

		//check whether the preference file exists
		File f = new File("/data/data/com.copelabs.oiframework/shared_prefs/DayTime.xml");
		if (!f.exists()){
			//If not, create preference file to keep track of day and sample
			if(debug){
				//writeToSD("Setting system day and slot");
			}
				
			daySample.putInt(day, dayCounter);
			daySample.putInt(dayNumber, dNumber);
			daySample.putInt(timeSlot, slot);
			daySample.commit();	
			
			if(debug){
				SharedPreferences prefs = this.context.getSharedPreferences("DayTime", Context.MODE_PRIVATE);
				//writeToSD("The app started on day " + prefs.getInt(day, 0) + " with number " + prefs.getInt(dayNumber, 0));
				//writeToSD("The app started on timeSlot " + prefs.getInt(timeSlot, 0));
			}
		}
		else{ //Application has started
			if(debug){
				//writeToSD("Application restarted !!!");
			}
			
			SharedPreferences prefs = this.context.getSharedPreferences("DayTime", Context.MODE_PRIVATE);	
			
			int savedDay = prefs.getInt(day, 0);
			int savedDayNumber = prefs.getInt(dayNumber, 0);
			int savedSample = prefs.getInt(timeSlot, 0);
			
			if(debug){
				//writeToSD("Day when app stopped: " +  savedDay + " with number " + savedDayNumber);
				//writeToSD("sample when app stopped: " + savedSample);
			}
			
			if(dNumber == savedDayNumber){ //Application restarted in the same day
				if(debug){
					//writeToSD("We are in the same day !!!");
				}
				if(slot == savedSample){ //Application restarted in the same time slot
					if(debug){
						//writeToSD("We are in the same slot !!! - Update encounter time to all connected devices");
					}
					appRestarted = true;
				}
				else{ //Application restarted in a new time slot
					if(debug){
						//writeToSD("We are in NEW slot !!!");
					}
					appRestarted = true;
					
					if(debug){
						//writeToSD("Current time !!!! " + currentTime );
					}
					
					int currentSample = slot;
					String currentDay = String.valueOf(OnSocialWeightUpdate.day);
					int samplesToUpdate = currentSample-savedSample;
					
					if(debug){
						//writeToSD("Current sample: " + currentSample );
						//writeToSD("Current day: " + currentDay );
						//writeToSD("Number of slots to update: " + samplesToUpdate );
					}
					
					int updateControl = 0;
					
					while(updateControl<samplesToUpdate){
					
						if(debug){
							//writeToSD("Updating social weights in daily sample " + (savedSample+updateControl) + " of Day " + savedDay);
						}

						newHour.computeSocialWeight(savedDay,savedDay, savedSample+updateControl, currentTime, appRestarted, false);

						updateControl++;
					}
					
					//Update preference file
					daySample.clear();
					daySample.putInt(day, dayCounter);
					daySample.putInt(dayNumber, dNumber);					
					daySample.putInt(timeSlot, currentSample);
					daySample.commit();
					
					if(debug){
						SharedPreferences updatedPrefs = this.context.getSharedPreferences("DayTime", Context.MODE_PRIVATE);	
						Log.i(TAG,"New Day stored: " +  updatedPrefs.getInt(day, 0) + " with number " + updatedPrefs.getInt(dayNumber, 0));
						Log.i(TAG,"New sample stored: " + updatedPrefs.getInt(timeSlot, 0));
						//writeToSD("New Day stored: " +  updatedPrefs.getInt(day, 0) + " with number " + updatedPrefs.getInt(dayNumber, 0));
						//writeToSD("New sample stored: " + updatedPrefs.getInt(timeSlot, 0));
					}
				}
			}else{//Application restarted in a new day
				if(debug){
					//writeToSD("We are NOT in the same day !!!");
				}
				appRestarted = true;
				
				if(debug){
					//writeToSD("Current time !!!! " + currentTime );
				}
				
				int currentSample = slot;
				int samplesToUpdatePreviousDay = 24 - savedSample;
				int samplesToUpdateCurrentDay = currentSample;
				int totalSamplesToUpdate = samplesToUpdatePreviousDay+samplesToUpdateCurrentDay;
				
				if(dNumber-savedDayNumber>1){
					int completeDays = dNumber-savedDayNumber-1;
					int moreSamples = completeDays*24;
					totalSamplesToUpdate = totalSamplesToUpdate + moreSamples;
				}
				
				if(debug){
					//writeToSD("totalSamplesToUpdate: " + totalSamplesToUpdate);
				}
				
				int updateControl = 0;
				int dayControl = savedDay;
				int indexUpdate = savedSample;
				
				boolean updateOverDiffDays = false;
				
				//Updating slots
				while(updateControl<totalSamplesToUpdate){
					
					if(indexUpdate==24){
						indexUpdate=0;
						dayControl++;
					}
					
					if(debug){
						//writeToSD("Updating social weights in daily sample " + indexUpdate + " of Day " + dayControl);
					}
					
					newHour.computeSocialWeight(savedDay, dayControl, indexUpdate, currentTime, appRestarted, updateOverDiffDays);
					
					updateOverDiffDays = true;

					updateControl++;
					indexUpdate++;
				}
				
				//Update preference file
				daySample.clear();
				dayCounter=dNumber-savedDayNumber+1;
				daySample.putInt(day, dayControl);
				daySample.putInt(dayNumber, dNumber);
				daySample.putInt(timeSlot, currentSample);
				daySample.commit();
				
				//Update day in OnNewHourUpdate				
				OnSocialWeightUpdate.day = dayControl;
				
				if(debug){
					SharedPreferences updatedPrefs = this.context.getSharedPreferences("DayTime", Context.MODE_PRIVATE);
					Log.i(TAG,"New Day stored: " +  updatedPrefs.getInt(day, 0) + " with number " + updatedPrefs.getInt(dayNumber, 0));
					Log.i(TAG,"New sample stored: " + updatedPrefs.getInt(timeSlot, 0));
					//writeToSD("New Day stored: " +  updatedPrefs.getInt(day, 0) + " with number " + updatedPrefs.getInt(dayNumber, 0));
					//writeToSD("New sample stored: " + updatedPrefs.getInt(timeSlot, 0));
				}
			}
		}
		
	}
	
	/**
     * This method checks whether the device is in DB 
     * @param deviceAdd The device MAC address.
     * @return true if device is in DB, false otherwise
     */
	public boolean isDeviceOnDB(String deviceAdd){
		return database.hasBTDevice(deviceAdd);
	}
	   
	/**
     * This method notifies a database change to the listeners.
     */
	private void notifyDataBaseChange () {
    	Intent i = new Intent(SocialWeight.socialWeight_key);
    	context.sendBroadcast(i);
    	}
	
	/**
     * This class allows to sort social weight entries in ascending order.
     */
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
    * This method provides the current time slot. 
    * @return currentTimeSlot The actual time slot.
    */
	public int getTimeSlot(){
		int currentTimeSlot = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return currentTimeSlot;
	}
	
	/**
	 * This method provides a list of contact based on the information of devices encountered.
	 * @return listOfContacts The list of contacts.
	 */
	public List<UserDevice> getContactList() {
		Map<String,UserDeviceInfo> tempListOfDeviceInfo = database.getAllBTDevice();
		ArrayList<UserDevice> listOfContacts = new ArrayList<UserDevice> ();
		Iterator<String> iterator = tempListOfDeviceInfo.keySet().iterator();

		while (iterator.hasNext()){
			String dev = iterator.next();
			UserDevice devInfo = new UserDevice(database.getBTDevice(dev).getDevAdd(),database.getBTDevice(dev).getDevName());
			listOfContacts.add(devInfo);
		}
		return listOfContacts;
	}
	
	/**
	 * This method provides a list of social weights for forwarding decisions.
	 * @parameter mDevices The list of devices to get the SW.
	 * @return swList The list of social weights.
	 */
	public Map<String, Integer> getSWList(ArrayList<String> mDevices) {
		Map<String, Integer> listWeights = new HashMap<String, Integer> ();
		int currentTimeSlot = getTimeSlot();
		
		for (String  mMAC: mDevices) {
			UserDevSocialWeight mUserDevSocialWeight= database.getBTDeviceSocialWeight(mMAC);
			if (mUserDevSocialWeight == null)
				continue;
			listWeights.put(mMAC,(int)mUserDevSocialWeight.getSocialWeight(currentTimeSlot));
		}
			
		return listWeights;
	}
	
	/**
	 * This method provides a list of all social weights to send to neighbors.
	 * @return listAllWeights The list of all computed social weights.
	 */
	public Map<String, Integer> getAllSWList() {
		Map<String,UserDeviceInfo> tempListOfDeviceSW = database.getAllBTDevice();
		Map<String, Integer> listAllWeights = new HashMap<String, Integer> ();
		Iterator<String> devIterator = tempListOfDeviceSW.keySet().iterator();
		int currentTimeSlot = getTimeSlot();
		
		while (devIterator.hasNext()){
			String btDev = devIterator.next();
			listAllWeights.put(btDev, (int)database.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot));
		}
		return listAllWeights;
	}

    /**
     * This method provides the name and the MAC Address of local Bluetooth adapter.
     * @return The local info or null
     */
    public List<String> getLocalInfo(){
    	if(myBTManager == null)
			return null;
    	return myBTManager.getLocalInfo();
    }
  
//    /**
//     * Writes on a file for Bluetooth debugging
//     */
//    private static void writeToSD(String text){
//    	File file = new File(Environment.getExternalStorageDirectory()+File.separator+"SocialProximity","SocialProximityOutput.txt");
//    	String currentTime = (String) DateFormat.format("dd/MM - hh:mm:ss.sss", Calendar.getInstance().getTime());
//    	try {
//			FileWriter writer = new FileWriter(file, true);
//			String line = currentTime + " " + text + "\n";
//			writer.write(line); 
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    }

	/**
	* This class allows is used to notify SocialProximity that a Bluetooth device has been found.
	**/
    class ServiceBTListener implements BTDeviceFinder {

		/**
		* This method handles information about the found Bluetooth device.
		* @param device The BluetoothDevice information.
		* @param btClass The BluetoothClass of the found device.
		**/
		public void onDeviceFound(BluetoothDevice device, BluetoothClass btClass) {
    		long currentTime = System.nanoTime(); 
    		
    		if(debug){ 
    			//writeToSD("Device found @ " + currentTime );
    		}
    		
    		if (filterDevice(device, btClass)){
				if(!isDeviceOnDB(device.getAddress()) && device.getName()!=null){
					//if(!device.getName().contains("Oi"))
						//return;
					
					if(debug){
						Log.i(TAG,"Adding " + device.getName() + " to database");
						//writeToSD("Adding " + device.getName() + " to database");
					}
					
					UserDeviceInfo btDev = new UserDeviceInfo();
					UserDevEncounterDuration duration = new UserDevEncounterDuration();
					UserDevAverageEncounterDuration averageDuration = new UserDevAverageEncounterDuration();
					UserDevSocialWeight socialWeight = new UserDevSocialWeight();
					
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
					
					database.registerNewBTDevice(btDev, duration, averageDuration, socialWeight);
					
					//notifies Routing of a contact list update
					notifyNewDeviceEntry();
					
					if(debug){
						//writeToSD("Device name: " + device.getName() + " now added in the database");
						//writeToSD("**************************");
					}
				}else{
					if(device.getName()==null)
						return;
					UserDeviceInfo btDev = database.getBTDevice(device.getAddress());
					UserDevEncounterDuration duration = database.getBTDeviceEncounterDuration(device.getAddress());
					
					Log.i(TAG,"onDeviceFound - SocialProximity");
		        	Log.i(TAG,"btDev.getDevName(): " + btDev.getDevName());
		        	Log.i(TAG,"btDev.getDevAdd(): " + btDev.getDevAdd());
		        	
					if(debug){
						Log.i(TAG,"Updating encounter duration for: " + btDev.getDevName() + " - MAC: " + btDev.getDevAdd());
						//writeToSD("Updating encounter duration for: " + btDev.getDevName() + " - MAC: " + btDev.getDevAdd());
					}

					//BTUserDevAverageEncounterDuration averageDuration = datasource.getBTDeviceAverageEncounterDuration(device.getAddress()) ;
					//BTUserDevSocialWeight socialWeight = datasource.getBTDeviceSocialWeight(device.getAddress());
					//long timeInVicinity = currentTime;
					//btDev.setTimeInVicinity(btDev.getTimeInVicinity() + timeInVicinity-btDev.getEncounterTime());
					
					long encounterEnd = currentTime;
					int currentTimeSlot = getTimeSlot();
					
					if(appRestarted){
						btDev.setEncounterTime(currentTime);
						database.updateBTDeviceAndDuration(btDev, duration);
					}
					
					double timeInContact = (encounterEnd-btDev.getEncounterStart())/1000000000.0;
					
					if(debug){
						//writeToSD("Old value for encounter duration: " + duration.getEncounterDuration(currentTimeSlot));
						//writeToSD("encounterEnd - BTCore: " + encounterEnd);
						//writeToSD("EncounterStart - BTCore: " + btDev.getEncounterStart());
						//writeToSD("timeInContact - BTCore: " + timeInContact);
					}

					///* A negative value indicates the application restarted.
					//* To avoid a negative effect, the new encounter duration remains with the previous value.
					//* May not be necessary as the appRestarted takes care of that.
					//*/
					//if(timeInContact<0.0){
					//	Log.i(TAG,"Negative value for encounter duration detected !! Resorting to previous value !! **** BluetoothCore ****");
					//	writeToSD("Negative value for encounter duration detected !! Resorting to previous value !! **** BluetoothCore ****");
					//	timeInContact = 0.0;	
					//}
					
					double newEncounterDuration = duration.getEncounterDuration(currentTimeSlot) + timeInContact;
					
					btDev.setEncounterTime(encounterEnd);
					duration.setEncounterDuration(currentTimeSlot, newEncounterDuration);
					database.updateBTDeviceAndDuration(btDev, duration);
					
					notifyDataBaseChange ();
					showDevicesOnDB();
				}
			}
		}
		
		 /**
	     * This method shows the Bluetooth devices stored on the DB. 
	     */
		public void showDevicesOnDB() {
			int numberDevOnDB = database.getAllBTDevice().entrySet().size();
			
			if(debug){
				//writeToSD("*************Data from DB*************");
				//writeToSD("Total number of neighbors: " + numberDevOnDB + " in the database");
			}
			
			Map<String,UserDeviceInfo> tempListOfDevice = database.getAllBTDevice(); 
			Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
			
			while (devIterator.hasNext()){
				String btDev = devIterator.next();
				
				if(debug){
					//writeToSD("Device name: " + (database.getBTDevice(btDev)).getDevName() + " in the database");
					//writeToSD("Device MAC: " + btDev);
				}			
				int timeSlot = 0;
				while(timeSlot<24){	
					if((database.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot)!=0.0){

						if(debug){
							//writeToSD("Encounter duration - Time Slot" + timeSlot + ": " + (database.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot));
							//writeToSD("Avg encounter duration - Time Slot" + timeSlot + ": " +  (database.getBTDeviceAverageEncounterDuration(btDev)).getAverageEncounterDuration(timeSlot));
							//writeToSD("Social weight - Time Slot" + timeSlot + ": " + (database.getBTDeviceSocialWeight(btDev)).getSocialWeight(timeSlot));
						}
						
					}					
					timeSlot++;
				}
				if(debug){
					//writeToSD("---------------------------------");
				}
			}
			if(debug){
				//writeToSD("************************************");
			}
		}
		
	    /**
	     * This method checks whether the device is of interest.
	     * Bluetooth devices can be of different types but only smart phones are considered.
	     * @param btClass The device class.
	     * @return true, if device is of interest, false otherwise.
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
			
    		if(debug){
    			//writeToSD("Device name: " + device.getName() + " - MAC: " + device.getAddress() + " - Type: " + devtype);
    		}
    		
			//if (btClass.getDeviceClass() == 524){
			//	return true;
			//}
			//
			//if(debug){
			//	writeToSD("Device of no interest");
			//}
			//    		
			//return false;
    		return true;
		}
	}

    /**
     * This method unregisters BroadcastReceiver, cancels the alarm, and closes the Bluetooth manager.
     */
	public void stop() {
    	context.unregisterReceiver(newHour);
    	alarmMgr.cancel(alarmIntent);
    	myBTManager.close(context);
	}

	
    /**
     * This method sets the listener that Routing will use to inform  
     * about update on the social list.
     * @param listener The listener to register
     */
	public void setSocialProximityListener(
			SocialProximityListener listener) {
				listeners.add(listener);
	}
	
    /**
     * This method notifies the Routing class that social list has been updated.
     */
    public void notifySWListUpdate() {
    	for (SocialProximityListener listener : listeners) 
    	{
    	    listener.notifySWListUpdate();
    	}
    }
    
    /**
     * This method notifies the Routing class of a contact list update.
     */
    public void notifyNewDeviceEntry() {
    	for (SocialProximityListener listener : listeners) 
    	{
    	    listener.notifyNewDeviceEntry();
    	}
    }
}
