/**
 * OnNewHourUpdate is responsible for the updates on encounter time, encounter duration, 
 * average encounter duration and social weight. These updates take place at every
 * new hour.
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class OnNewHourUpdate extends BroadcastReceiver{
    public static final String NEW_HOUR = "android.intent.action.NEWHOUR";
    private DatabaseFunctions datasource;
	private int day = 1;
    private ArrayList<DataBaseChangeListener> listeners = new ArrayList<DataBaseChangeListener> ();
    
    /**
     * OnNewHourUpdate constructor.
     * @param datasource2 The DatabaseFunctions object that allows the creation and
     * handling of tables for social proximity computation.
     */
	public OnNewHourUpdate(DatabaseFunctions datasource2) {
		datasource = datasource2;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (OnNewHourUpdate.NEW_HOUR.equals(action)) {
			Log.i("BT","!!!!!! OnNewHourUpdate !!!!!!");
			//writeToSD("!!!!!! OnNewHourUpdate !!!!!!");
		}
		
		long currentTime = System.nanoTime(); 
		
		Log.i("BT","Current time !!!! " + currentTime );
		//writeToSD("Current time !!!! " + currentTime );
		
		int currentTimeSlot = getTimeSlot();
		int previousTimeSlot = currentTimeSlot-1;
		
		if(previousTimeSlot==-1){
			previousTimeSlot = 23;
			day++;
		}
			
	
			
		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
		
		while (devIterator.hasNext()){
			String btDev = devIterator.next();
			
			BTUserDevice btDevice = datasource.getBTDevice(btDev);
			BTUserDevEncounterDuration duration = datasource.getBTDeviceEncounterDuration(btDev);
			BTUserDevAverageEncounterDuration averageDuration = datasource.getBTDeviceAverageEncounterDuration(btDev) ;
			BTUserDevSocialWeight socialWeight = datasource.getBTDeviceSocialWeight(btDev);
			
			Log.i("BT","Device name: " + btDevice.getDevName() + " in the database");
			Log.i("BT","Device MAC: " + btDev);
			Log.i("BT","Encounter start: " +  btDevice.getEncounterStart());
			Log.i("BT","Encounter duration - timeSlot " +  (previousTimeSlot) + ": " + 
					duration.getEncounterDuration(previousTimeSlot));
			Log.i("BT","Average encounter duration - timeSlot " +  (previousTimeSlot) + ": "+ 
					averageDuration.getAverageEncounterDuration(previousTimeSlot));
			Log.i("BT","Social weight - timeSlot " +  (previousTimeSlot) + ": "+ 
					socialWeight.getSocialWeight(previousTimeSlot));
			
			//writeToSD("Device name: " + btDevice.getDevName() + " in the database");
			//writeToSD("Device MAC: " + btDev);
			//writeToSD("Encounter start: " +  btDevice.getEncounterStart());
			//writeToSD("Encounter duration - timeSlot " +  (previousTimeSlot) + ": " +	duration.getEncounterDuration(previousTimeSlot));
			//writeToSD("Average encounter duration - timeSlot " +  (previousTimeSlot) + ": "+ averageDuration.getAverageEncounterDuration(previousTimeSlot));
			//writeToSD("Social weight - timeSlot " +  (previousTimeSlot) + ": "+ socialWeight.getSocialWeight(previousTimeSlot));
			

			//writeToSD("Current day: " + day);
			//For the previous time slot:
			//Update encounter duration
			//TODO Update duration of encounters that are active when the new hour changes ***************
//			double newEncounterDuration = (currentTime-btDevice.getEncounterStart())/1000000000.0;
//			//writeToSD("newEncounterDuration - NewHour: " + newEncounterDuration);
//			duration.setEncounterDuration(previousTimeSlot, newEncounterDuration);
			
			//writeToSD("Encounter duration updated to new hour: " + duration.getEncounterDuration(previousTimeSlot));
			
			//Set new encounter start
			btDevice.setEncounterTime(currentTime);
			datasource.updateBTDeviceAndDuration(btDevice, duration);
			
			//writeToSD("New encounter start: " +  btDevice.getEncounterStart());
			
			//Update average encounter duration
			double avgEncDuration_old = averageDuration.getAverageEncounterDuration(previousTimeSlot);	
			Log.i("BT","avgEncDuration_old: " + avgEncDuration_old);
			//writeToSD("avgEncDuration_old: " + avgEncDuration_old);
			
//			double avgEncDuration_new = (duration.getEncounterDuration(previousTimeSlot)/1000000000 + (day-1) * avgEncDuration_old)/day;
			double avgEncDuration_new = (duration.getEncounterDuration(previousTimeSlot) + (day-1) * avgEncDuration_old)/day;
			Log.i("BT","avgEncDuration_new: " + avgEncDuration_new);
			
			averageDuration.setAverageEncounterDuration(previousTimeSlot, avgEncDuration_new);
			datasource.updateBTDevAvgEncounterDuration(averageDuration);
			
			//writeToSD("New avg encounter duration: " + averageDuration.getAverageEncounterDuration(previousTimeSlot));
		
			//Update social weight
			double k = 0;
			int index = previousTimeSlot;
			double dailySampleNumber = 24;
			double sw  = 0.0;
//			double swTemp = 0.0;
			
			Log.i("BT","Calculating weight!!!");
			//writeToSD("Calculating weight!!!");
			
			while(k<24){
				if(index == 24)
					index = 0;
				
				double levels = dailySampleNumber/(dailySampleNumber+k);
				double avgDurationPreviousSlot = (double)averageDuration.getAverageEncounterDuration(index);
				Log.i("BT","k: " + k);
				Log.i("BT","index: " + index);
				Log.i("BT","avgDurationPreviousSlot: " + avgDurationPreviousSlot);
				Log.i("BT","sw initial: " + sw);
//				Log.i("BT","swTemp initial: " + swTemp);
				
				Log.i("BT","dailySampleNumber/(dailySampleNumber+k): " + levels);
				Log.i("BT","ds*ad: " + levels * avgDurationPreviousSlot);
				
				//writeToSD("k: " + k);
				//writeToSD("index: " + index);
				//writeToSD("avgDurationPreviousSlot: " + avgDurationPreviousSlot);
				//writeToSD("sw initial: " + sw);
//				//writeToSD("swTemp initial: " + swTemp);
				//writeToSD("dailySampleNumber/(dailySampleNumber+k): " + levels);
				//writeToSD("ds*ad: " + levels * avgDurationPreviousSlot);
				
				sw = sw + (levels * avgDurationPreviousSlot);
//				sw = swTemp + (levels * avgDurationPreviousSlot);
//				swTemp = sw;
				
				Log.i("BT","sw new: " + sw);
//				Log.i("BT","swTemp new: " + swTemp);
				
				//writeToSD("sw new: " + sw);
//				//writeToSD("swTemp new: " + swTemp);
				
				index++;
				k++;
			}
//			socialWeight.setSocialWeight(previousTimeSlot, swTemp);
			socialWeight.setSocialWeight(previousTimeSlot, sw);
			datasource.updateBTDevSocialWeight(socialWeight);
			
			//writeToSD("Social weight: " + socialWeight.getSocialWeight(previousTimeSlot));
//			duration.setEncounterDuration(currentTimeSlot, currentTime-(datasource.getBTDevice(btDev)).getEncounterStart());
//			
////			datasource.updateBTDevice(btDevice, duration);
//			Log.i("BT","Current day: " + day);
////			Log.i("BT","Encounter duration updated to new hour: " +
////					(double)(datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(previousTimeSlot)/1000000000);
//			Log.i("BT","Encounter duration updated to new hour: " +
//					duration.getEncounterDuration(previousTimeSlot));
//			Log.i("BT","New encounter start: " +  btDevice.getEncounterStart());
//			Log.i("BT","Avg encounter duration: " +
//					averageDuration.getAverageEncounterDuration(previousTimeSlot));
//			Log.i("BT","Social weight: " +
//					socialWeight.getSocialWeight(previousTimeSlot));
//			
//			//writeToSD("Current day: " + day);
//			//writeToSD("Encounter duration updated to new hour: " +
//					duration.getEncounterDuration(previousTimeSlot));
//			//writeToSD("New encounter start: " +  btDevice.getEncounterStart());
//			//writeToSD("Avg encounter duration: " +
//					averageDuration.getAverageEncounterDuration(previousTimeSlot));
//			//writeToSD("Social weight: " +
//					socialWeight.getSocialWeight(previousTimeSlot));
			Log.i("BT","---------------------------------");
			//writeToSD("---------------------------------");
			notifyDataBaseChange ();
		}
		
    	showDevicesOnDB();
//   	 
//   	BTUserDevice btDev = datasource.getBTDevice(device.getAddress());
//   	Log.i("BT","device: " + btDev.getDevAdd() + " " + btDev.getDevName() + " " + btDev.getEncounterStart());
	}

	 /**
     * Provides the current time slot.
     * @return currentTimeSlot The actual time slot.
     */
	public int getTimeSlot(){
		int currentTimeSlot = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		Log.i("BT","Current hour(slot): " + currentTimeSlot);
		
		//writeToSD("Current hour(slot): " + currentTimeSlot);
		
		return currentTimeSlot;
	}
	
	 /**
     * Shows the Bluetooth devices stored on the DB. 
     * For debugging purposes. 
     */
	public void showDevicesOnDB() {
		int numberDevOnDB = datasource.getAllBTDevice().entrySet().size();
		
		Log.i("BT","*************Data from DB - OnNewHourUpdate *************");
		Log.i("BT","Total number of neighbors: " + numberDevOnDB + " in the database");
		
		//writeToSD("*************Data from DB - OnNewHourUpdate *************");
		//writeToSD("Total number of neighbors: " + numberDevOnDB + " in the database");
						
		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 
		Map<String,BTUserDevEncounterDuration> tempListOfDevEncounterDuration = datasource.getAllBTDevEncounterDuration(); 
		
//		Set<String> deviceSet = tempListOfDevice.keySet();
		
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
		
		while (devIterator.hasNext()){
			String btDev = devIterator.next();
//			Log.i("BT","String btDev: " + btDev);
			Log.i("BT","Device name: " + (datasource.getBTDevice(btDev)).getDevName() + " in the database");
			Log.i("BT","Device MAC: " + btDev);
			
			//writeToSD("Device name: " + (datasource.getBTDevice(btDev)).getDevName() + " in the database");
			//writeToSD("Device MAC: " + btDev);
							
			int timeSlot = 0;
//			while(devEncDurationIterator.hasNext()){
//			Log.i("BT","Device encounter duration: ");
			while(timeSlot<24){	
//				String btDevEncDuration = devEncDurationIterator.next();
				
				if((double)(datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot)!=0.0){
					Log.i("BT","Encounter duration - Time Slot" + timeSlot + ": " + 
							(double)(datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot));
					Log.i("BT","Avg encounter duration - Time Slot" + timeSlot + ": " + 
							(double)(datasource.getBTDeviceAverageEncounterDuration(btDev)).getAverageEncounterDuration(timeSlot));
					Log.i("BT","Social weight - Time Slot" + timeSlot + ": " + 
							(double)(datasource.getBTDeviceSocialWeight(btDev)).getSocialWeight(timeSlot));
					
					//writeToSD("Encounter duration - Time Slot" + timeSlot + ": " + (double)(datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot));
					//writeToSD("Avg encounter duration - Time Slot" + timeSlot + ": " + (double)(datasource.getBTDeviceAverageEncounterDuration(btDev)).getAverageEncounterDuration(timeSlot));
					//writeToSD("Social weight - Time Slot" + timeSlot + ": " + (double)(datasource.getBTDeviceSocialWeight(btDev)).getSocialWeight(timeSlot));
				}
				timeSlot++;
//				Log.i("BT","Time Slot++" + timeSlot);
			}
			Log.i("BT","---------------------------------");
			//writeToSD("---------------------------------");
		}
		Log.i("BT","************************************");
		//writeToSD("************************************");
	}
	
	/**
     * Notifies a database change to the listeners.
     */
    private void notifyDataBaseChange () {
    	for (DataBaseChangeListener listener : this.listeners) 
    	{
    	    listener.onDataBaseChangeBT(new ArrayList<BTUserDevice>(datasource.getAllBTDevice().values()));
    	    listener.onDataBaseChangeBTEncDur(new ArrayList<BTUserDevEncounterDuration>(datasource.getAllBTDevEncounterDuration().values()));
    	    listener.onDataBaseChangeBTAvgEncDur(new ArrayList<BTUserDevAverageEncounterDuration>(datasource.getAllBTDevAverageEncounterDuration().values()));
    	    listener.onDataBaseChangeBTSocialWeight(new ArrayList<BTUserDevSocialWeight>(datasource.getAllBTDevSocialWeight().values()));
    	}
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
