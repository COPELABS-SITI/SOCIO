package com.copelabs.oiframework.socialproximity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application. 
 * This class is responsible for computing the social weight among users.
 * @author Waldir Moreira (COPELABS/ULHT)
 */
public class OnSocialWeightUpdate extends BroadcastReceiver{
	private final String TAG = "Social Proximity";
	private DataBase database;
	public static int day = 1;
	SocialProximity callback;
	
	 // for debugging purposes
	 private boolean debug = true;
	
    public static final String NEW_HOUR =
            "android.intent.action.NEWHOUR";
    
	/**
	* This method is the constructor for OnSocialWeightUpdate.
	* @param database2 The database to be used.
	* @param callback The SocialProximity object to allow notification on social weight changes.
	**/
	public OnSocialWeightUpdate(DataBase database2, SocialProximity callback) {
		database = database2;
		this.callback = callback;
	}

	private ArrayList<DataBaseChangeListener> listeners = new ArrayList<DataBaseChangeListener> ();
	
	/**
	* This method receives action concerning social weight updates.
	* @param context The context.
	* @param intent The intent.
	**/
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (OnSocialWeightUpdate.NEW_HOUR.equals(action)) {
			if(debug){
				Log.i(TAG,"!!!!!! OnSocialWeightUpdate !!!!!!");
				//writeToSD("!!!!!! OnSocialWeightUpdate !!!!!!");
			}
		}
		
		long currentTime = System.nanoTime(); 
		
		if(debug){
			//writeToSD("Current time !!!! " + currentTime );
		}
		
		int currentTimeSlot = getTimeSlot();
		//int previousTimeSlot = currentTimeSlot-1;     **************** Uncomment for hour-based SW updates
		int previousTimeSlot = currentTimeSlot;       // 4-minute-based SW updates, Comment this for hour-based SW updates
		
		//To allow the computation for last daily sample
		if(previousTimeSlot==-1){
			previousTimeSlot = 23;
		}
		
		boolean appRestart = SocialProximity.appRestarted;
		
		//This means the beginning of a new day		// 4-minute-based SW updates, Comment this for hour-based SW updates		
		if(currentTimeSlot==0){
			day++;
		}
		
		computeSocialWeight(day, day, previousTimeSlot, currentTime, appRestart, false);
		
//		//This means the beginning of a new day		 **************** Uncomment for hour-based SW updates		
//		if(currentTimeSlot==0){
//			day++;
//		}

		//Update preference file
		Calendar c = Calendar.getInstance();
		String d = "day";
		String dayNumber = "dayNumber";
		String timeSlot = "timeSlot";
		int dNumber = c.get(Calendar.DAY_OF_YEAR);
		if(debug){
			//writeToSD("Updating pref files with Day " + day + " with dayNumber " + dNumber + " at new timeSlot " + currentTimeSlot);
		}
		SharedPreferences.Editor daySample = context.getSharedPreferences("DayTime", Context.MODE_PRIVATE).edit();	
		daySample.clear();
		daySample.putInt(d, day);
		daySample.putInt(dayNumber, dNumber);
		daySample.putInt(timeSlot, currentTimeSlot);
		daySample.commit();
		
		if(debug){
			SharedPreferences prefs = context.getSharedPreferences("DayTime", Context.MODE_PRIVATE);
			//writeToSD("Updated prefFiles: Day " + prefs.getInt(d, 0) + " with number " + prefs.getInt(dayNumber, 0) + " at new timeSlot " + prefs.getInt(timeSlot, 0));
		}
	}
	
	
	/**
     * This method computes the social weight towards all encountered nodes 
     *  @param savDay The day the app started running.
     *  @param currDay The current day when the app restarted.
     *  @param currTimeSlot The current time slot when the app restarted.
     *  @param currTime The current time when the app restarted.
     *  @param appR The flag that tells whether the app restarted.
     *  @param updOverDiffDays The flag that indicates the updates occur over different days.
     */
	public void computeSocialWeight(int savDay, int currDay, int currTimeSlot, long currTime, boolean appR, boolean updOverDiffDays){
		int savedDay = savDay;
		int currentDay = currDay;
		int currentTimeSlot = currTimeSlot;
		long currentTime = currTime;
		boolean appRestart = appR;
		boolean updateOverDiffDays = updOverDiffDays;
		
		Map<String,UserDeviceInfo> tempListOfDevice = database.getAllBTDevice(); 
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
		
		while (devIterator.hasNext()){
			String btDev = devIterator.next();
			
			UserDeviceInfo btDevice = database.getBTDevice(btDev);
			UserDevEncounterDuration duration = database.getBTDeviceEncounterDuration(btDev);
			UserDevAverageEncounterDuration averageDuration = database.getBTDeviceAverageEncounterDuration(btDev) ;
			UserDevSocialWeight socialWeight = database.getBTDeviceSocialWeight(btDev);
			
			if(appRestart){
				if(debug)
					//writeToSD("AppRestarted in OnNewHourUpdate - Update encounter time to current time: " + currentTime);
				btDevice.setEncounterTime(currentTime);
				
				if(currentDay!=savedDay || updateOverDiffDays){
					if(debug)
						//writeToSD("Different day or Old value of encounterDuration found, so zero the encounter duration!!!");
					duration.setEncounterDuration(currentTimeSlot, 0.0);
				}
				database.updateBTDeviceAndDuration(btDevice, duration);
			}
			
			if(debug){
				//writeToSD("Device name: " + btDevice.getDevName() + " in the database");
				//writeToSD("Device MAC: " + btDev);
				//writeToSD("Encounter start: " +  btDevice.getEncounterStart());
				//writeToSD("Encounter duration - timeSlot " +  (currentTimeSlot) + ": " + duration.getEncounterDuration(currentTimeSlot));
				//writeToSD("Average encounter duration - timeSlot " +  (currentTimeSlot) + ": "+ averageDuration.getAverageEncounterDuration(currentTimeSlot));
				//writeToSD("Social weight - timeSlot " +  (currentTimeSlot) + ": "+ socialWeight.getSocialWeight(currentTimeSlot));
				//writeToSD("Current day: " + currentDay);
			}
			
			double timeInContact = (currentTime-btDevice.getEncounterStart())/1000000000.0;
			
			///* A negative value indicates the application restarted.
			//* To avoid a negative effect, the new encounter duration remains with the previous value.
			//* May not be necessary as the appRestarted takes care of that.
			//*/
			//if(timeInContact<0.0){
			//	writeToSD("Negative value for encounter duration detected !! Resorting to previous value !! **** OnHourUpdate ****");
			//	timeInContact = 0.0;	
			//}
		
			double newEncounterDuration = duration.getEncounterDuration(currentTimeSlot) + timeInContact;
			
			btDevice.setEncounterTime(currentTime);
			duration.setEncounterDuration(currentTimeSlot, newEncounterDuration);
			database.updateBTDeviceAndDuration(btDevice, duration);
			
			if(debug){
				//writeToSD("New encounter start: " +  btDevice.getEncounterStart());
			}
			
			//Update average encounter duration
			double avgEncDuration_old = averageDuration.getAverageEncounterDuration(currentTimeSlot);	
			if(debug){
				//writeToSD("avgEncDuration_old: " + avgEncDuration_old);
			}
			
			double avgEncDuration_new = (duration.getEncounterDuration(currentTimeSlot) + ((currentDay-1) * avgEncDuration_old))/currentDay;
			if(debug){
				//writeToSD("avgEncDuration_new: " + avgEncDuration_new);
			}
			
			averageDuration.setAverageEncounterDuration(currentTimeSlot, avgEncDuration_new);
			database.updateBTDevAvgEncounterDuration(averageDuration);
			
			if(debug){
				//writeToSD("New avg encounter duration from DB: " + database.getBTDeviceAverageEncounterDuration(btDev).getAverageEncounterDuration(currentTimeSlot));
			}
		
			//Update social weight
			double k = 0;
			int index = currentTimeSlot;
			double dailySampleNumber = 24;
			double sw  = 0.0;

			if(debug){
				//writeToSD("Calculating weight!!!");
			}
			
			while(k<24){
				if(index == 24)
					index = 0;
				
				double levels = dailySampleNumber/(dailySampleNumber+k);
				double avgDurationPreviousSlot = (double)averageDuration.getAverageEncounterDuration(index);
				
				if(debug){
					//writeToSD("k: " + k);
					//writeToSD("index: " + index);
					//writeToSD("avgDurationPreviousSlot: " + avgDurationPreviousSlot);
					//writeToSD("sw initial: " + sw);
					//writeToSD("dailySampleNumber/(dailySampleNumber+k): " + levels);
					//writeToSD("ds*ad: " + levels * avgDurationPreviousSlot);
				}
				
				sw = sw + (levels * avgDurationPreviousSlot);
				
				if(debug){
					//writeToSD("sw new: " + sw);
				}
				
				index++;
				k++;
			}
			
			if(debug){
				//writeToSD("sw new again: " + sw);
			}
			
			socialWeight.setSocialWeight(currentTimeSlot, sw);
			database.updateBTDevSocialWeight(socialWeight);

			if(debug){
				//writeToSD("Social weight: " + socialWeight.getSocialWeight(currentTimeSlot));
				//writeToSD("Social weight from DB: " + database.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot));
				//writeToSD("---------------------------------");
			}
			
			notifyDataBaseChange ();
			
			//notify update of social weight list
			callback.notifySWListUpdate();
		}
    	showDevicesOnDB();
	}

	/**
     * This method provides the current time slot.
     * @return currentTimeSlot The actual time slot.
     */
	public int getTimeSlot(){
		int currentTimeSlot = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		if(debug){
			//writeToSD("Current hour(slot): " + currentTimeSlot);
		}
		
		return currentTimeSlot;
	}
	
	/**
     * This method shows the Bluetooth devices stored on the DB. 
     */
	public void showDevicesOnDB() {
		int numberDevOnDB = database.getAllBTDevice().entrySet().size();
		
		if(debug){
			//writeToSD("*************Data from DB - OnNewHourUpdate *************");
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
				
				if((double)(database.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot)!=0.0){
					
					if(debug){
						//writeToSD("Encounter duration - Time Slot" + timeSlot + ": " + (double)(database.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot));
						//writeToSD("Avg encounter duration - Time Slot" + timeSlot + ": " + (double)(database.getBTDeviceAverageEncounterDuration(btDev)).getAverageEncounterDuration(timeSlot));
						//writeToSD("Social weight - Time Slot" + timeSlot + ": " + (double)(database.getBTDeviceSocialWeight(btDev)).getSocialWeight(timeSlot));
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
     * This method notifies a database change to the listeners.
     */
    private void notifyDataBaseChange () {
    	for (DataBaseChangeListener listener : this.listeners){
    	    listener.onDataBaseChangeUserDevice(new ArrayList<UserDeviceInfo>(database.getAllBTDevice().values()));
    	    listener.onDataBaseChangeEncDur(new ArrayList<UserDevEncounterDuration>(database.getAllBTDevEncounterDuration().values()));
    	    listener.onDataBaseChangeAvgEncDur(new ArrayList<UserDevAverageEncounterDuration>(database.getAllBTDevAverageEncounterDuration().values()));
    	    listener.onDataBaseChangeSocialWeight(new ArrayList<UserDevSocialWeight>(database.getAllBTDevSocialWeight().values()));     	
    	}
    }
    
//    /**
//     * Writes on a file for Bluetooth debugging
//     */
//    private void writeToSD(String text){
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

}
