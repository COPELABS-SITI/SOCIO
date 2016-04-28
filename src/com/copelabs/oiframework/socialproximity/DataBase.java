package com.copelabs.oiframework.socialproximity;

import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application. 
 * This class provides the Database used by Social Proximity class for creation and 
 * handling of tables hold information for social weight computation.
 * @author Waldir Moreira (COPELABS/ULHT)
 */
public class DataBase {
	private SQLiteDatabase db;
	private boolean isDbOpen;
	private SQLiteHelper dbHelper;
	
	/**
	* This method is the constructor for DataBase.
	* @param context The context.
	**/
	public DataBase (Context context) {
		dbHelper = new SQLiteHelper(context);
		isDbOpen = false;		
	}
		
	 /**
	 * This method opens the predefined database.
	 * @param writable Tha flag to determine if database is writable
	 * @throws SQLException
	 **/
	public void openDB(boolean writable) throws SQLException {
		if (!isDbOpen) {
			if (writable)
				db = dbHelper.getWritableDatabase();
			else
				db = dbHelper.getReadableDatabase();
		}
	}
	
	/**
	 * This method closes the predefined database.
	 */
	public void closeDB() {
		dbHelper.close();
		isDbOpen = false;
	}

	/*
	 * BTDEVICE TABLE: This table stores information
	 * regarding BTDEVICES.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_NAME 
	 * 		- BTDEV_ENCOUNTERTIME
	 * 
	 * BTDEVICEENCOUNTERDURATION TABLE: This table stores information
	 * regarding the duration that found BTDEVICES are within communication range.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_ENCOUNTERDURATION
	 * 
	 * BTDEVICEAVERAGEENCOUNTERDURATION TABLE: This table stores information
	 * regarding the average duration of encounters towards found BTDEVICES.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_AVGENCOUNTERDURATION		 
	 * 
	 * BTDEVICESOCIALWEIGHT TABLE: This table stores information
	 * regarding the social weight towards found BTDEVICES.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_SOCIALWEIGHT		 
	 * 
	 */
	
	/**
	 * List of all columns on the BTDEVICE table.
	 */
	private String[] allColumnsBTDevices = { 
		SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
		SQLiteHelper.COLUMN_BTDEV_NAME,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART
	};
	
	/**
	 * List of all columns on the BTDEVICEENCOUNTERDURATION table.
	 */
	private String[] allColumnsBTDeviceEncounterDuration = { 
		SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23,
		SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24
	};
	
	/**
	 * List of all columns on the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 */
	private String[] allColumnsBTDeviceAverageEncounterDuration = { 
		SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23,
		SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24
	};	
	
	/**
	 * List of all columns on the BTDEVICESOCIALWEIGHT table.
	 */
	private String[] allColumnsBTDeviceSocialWeight = { 
		SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23,
		SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24
	};
		
	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICE table to a BTDevice object.
	 * @param cursor The cursor pointing to a record of the BTDEVICE table.
	 * @return btDev The BTDevice object
	 */
	private UserDeviceInfo cursorToBTDevice(Cursor cursor) {
		UserDeviceInfo btDev = new UserDeviceInfo();
		btDev.setDevAdd(cursor.getString(0));
		btDev.setDevName(cursor.getString(1));
		btDev.setEncounterTime(cursor.getLong(2));
		//btDev.setTimeInVicinity(cursor.getLong(5)); 
		return btDev;
	}

	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICEENCOUNTERDURATION table to a BTUserDevEncounterDuration object.
	 * @param cursor The cursor pointing to a record of the BTDEVICEENCOUNTERDURATION table.
	 * @return duration The BTUserDevEncounterDuration object.
	 */
	private UserDevEncounterDuration cursorToBTDevEncounterDuration(Cursor cursor) {
		UserDevEncounterDuration duration = new UserDevEncounterDuration();
		duration.setDevAdd(cursor.getString(0));
		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			duration.setEncounterDuration(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return duration;
	}
	
	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICEAVERAGEENCOUNTERDURATION table to a BTUserDevAverageEncounterDuration object.
	 * @param cursor The cursor pointing to a record of the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return averageDuration The BTUserDevAverageEncounterDuration object.
	 */
	private UserDevAverageEncounterDuration cursorToBTDevAverageEncounterDuration(Cursor cursor) {
		UserDevAverageEncounterDuration averageDuration = new UserDevAverageEncounterDuration();
		averageDuration.setDevAdd(cursor.getString(0));
		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			averageDuration.setAverageEncounterDuration(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return averageDuration;
	}
		
	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICESOCIALWEIGHT table to a BTUserDevSocialWeight object.
	 * @param cursor The cursor pointing to a record of the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return socialWeight The BTUserDevSocialWeight object.
	 */
	private UserDevSocialWeight cursorToBTDevSocialWeight(Cursor cursor) {
		UserDevSocialWeight socialWeight = new UserDevSocialWeight();
		socialWeight.setDevAdd(cursor.getString(0));
		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			socialWeight.setSocialWeight(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return socialWeight;
	}

	/**
	 * This method gets the number of records in the BTDEVICE table. 
	 * This is, the number of BTDevice registered on the application.
	 * @return The number of BTDevice registered by the application.
	 */
	public long getNumBTDevice(){
		return DatabaseUtils.queryNumEntries(db, SQLiteHelper.TABLE_BTDEVICE);
	}
		
	/**
	 * This method registers a new BTDevice in the application. 
	 * It creates a new record on the BTDEVICE, BTDEVICEENCOUNTERDURATION, 
	 * BTDEVICEAVERAGEENCOUNTERDURATION, and BTDEVICESOCIALWEIGHT tables,
	 * with the information passed as BTDevice.
	 * @param btDev The Bluetooth device information.
	 * @param duration The Bluetooth device information regarding the duration that the BT device is within communication range of others.
	 * @param averageDuration The Bluetooth device information regarding the average duration of encounter between the BT device and other devices.
	 * @param socialWeight The Bluetooth device information regarding the social weight of the BT device towards others.
	 */
	public void registerNewBTDevice (UserDeviceInfo btDev, UserDevEncounterDuration duration, 
			UserDevAverageEncounterDuration averageDuration, UserDevSocialWeight socialWeight) {
		
		ContentValues values = new ContentValues();
	    values.put(SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, btDev.getDevAdd());
	    values.put(SQLiteHelper.COLUMN_BTDEV_NAME, btDev.getDevName());
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART, btDev.getEncounterStart());
	    db.insert(SQLiteHelper.TABLE_BTDEVICE, null, values);

	    values = new ContentValues();
	    values.put(SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, duration.getDevAdd());
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1, duration.getEncounterDuration(0));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2, duration.getEncounterDuration(1));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3, duration.getEncounterDuration(2));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4, duration.getEncounterDuration(3));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5, duration.getEncounterDuration(4));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6, duration.getEncounterDuration(5));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7, duration.getEncounterDuration(6));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8, duration.getEncounterDuration(7));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9, duration.getEncounterDuration(8));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10, duration.getEncounterDuration(9));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11, duration.getEncounterDuration(10));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12, duration.getEncounterDuration(11));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13, duration.getEncounterDuration(12));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14, duration.getEncounterDuration(13));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15, duration.getEncounterDuration(14));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16, duration.getEncounterDuration(15));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17, duration.getEncounterDuration(16));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18, duration.getEncounterDuration(17));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19, duration.getEncounterDuration(18));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20, duration.getEncounterDuration(19));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21, duration.getEncounterDuration(20));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22, duration.getEncounterDuration(21));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23, duration.getEncounterDuration(22));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24, duration.getEncounterDuration(23));
	    db.insert(SQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, null, values);
    
	    values = new ContentValues();
	    values.put(SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, averageDuration.getDevAdd());
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1, averageDuration.getAverageEncounterDuration(0));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2, averageDuration.getAverageEncounterDuration(1));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3, averageDuration.getAverageEncounterDuration(2));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4, averageDuration.getAverageEncounterDuration(3));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5, averageDuration.getAverageEncounterDuration(4));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6, averageDuration.getAverageEncounterDuration(5));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7, averageDuration.getAverageEncounterDuration(6));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8, averageDuration.getAverageEncounterDuration(7));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9, averageDuration.getAverageEncounterDuration(8));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10, averageDuration.getAverageEncounterDuration(9));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11, averageDuration.getAverageEncounterDuration(10));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12, averageDuration.getAverageEncounterDuration(11));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13, averageDuration.getAverageEncounterDuration(12));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14, averageDuration.getAverageEncounterDuration(13));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15, averageDuration.getAverageEncounterDuration(14));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16, averageDuration.getAverageEncounterDuration(15));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17, averageDuration.getAverageEncounterDuration(16));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18, averageDuration.getAverageEncounterDuration(17));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19, averageDuration.getAverageEncounterDuration(18));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20, averageDuration.getAverageEncounterDuration(19));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21, averageDuration.getAverageEncounterDuration(20));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22, averageDuration.getAverageEncounterDuration(21));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23, averageDuration.getAverageEncounterDuration(22));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24, averageDuration.getAverageEncounterDuration(23));
	    db.insert(SQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, null, values);
    
	    values = new ContentValues();
	    values.put(SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, socialWeight.getDevAdd());
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1, socialWeight.getSocialWeight(0));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2, socialWeight.getSocialWeight(1));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3, socialWeight.getSocialWeight(2));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4, socialWeight.getSocialWeight(3));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5, socialWeight.getSocialWeight(4));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6, socialWeight.getSocialWeight(5));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7, socialWeight.getSocialWeight(6));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8, socialWeight.getSocialWeight(7));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9, socialWeight.getSocialWeight(8));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10, socialWeight.getSocialWeight(9));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11, socialWeight.getSocialWeight(10));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12, socialWeight.getSocialWeight(11));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13, socialWeight.getSocialWeight(12));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14, socialWeight.getSocialWeight(13));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15, socialWeight.getSocialWeight(14));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16, socialWeight.getSocialWeight(15));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17, socialWeight.getSocialWeight(16));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18, socialWeight.getSocialWeight(17));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19, socialWeight.getSocialWeight(18));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20, socialWeight.getSocialWeight(19));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21, socialWeight.getSocialWeight(20));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22, socialWeight.getSocialWeight(21));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23, socialWeight.getSocialWeight(22));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24, socialWeight.getSocialWeight(23));
	    db.insert(SQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, null, values);
	    //return db.insert(UsenseSQLiteHelper.TABLE_BTDEVICE, null, values);
	}
		
	/**
	 * This method updates a BTDevice already registered by the application. 
	 * This modifies the corresponding record to the BTDevice in the 
	 * BTDEVICE, BTDEVICEENCOUNTERDURATION, BTDEVICEAVERAGEENCOUNTERDURATION, 
	 * and BTDEVICESOCIALWEIGHT tables.
	 * @param btDev The Bluetooth device information.
	 * @param duration The Bluetooth device information regarding the duration that the BT device is within communication range of others.
	 */
	public void updateBTDeviceAndDuration(UserDeviceInfo btDev, UserDevEncounterDuration duration){
		String identifier = SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDev.getDevAdd() + "'";
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_BTDEV_NAME, btDev.getDevName());
		values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART, btDev.getEncounterStart());
		db.update(SQLiteHelper.TABLE_BTDEVICE, values, identifier, null);
			
		values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, duration.getDevAdd());
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1, duration.getEncounterDuration(0)); 
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2, duration.getEncounterDuration(1));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3, duration.getEncounterDuration(2));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4, duration.getEncounterDuration(3));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5, duration.getEncounterDuration(4));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6, duration.getEncounterDuration(5));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7, duration.getEncounterDuration(6));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8, duration.getEncounterDuration(7));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9, duration.getEncounterDuration(8));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10, duration.getEncounterDuration(9));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11, duration.getEncounterDuration(10));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12, duration.getEncounterDuration(11));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13, duration.getEncounterDuration(12));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14, duration.getEncounterDuration(13));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15, duration.getEncounterDuration(14));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16, duration.getEncounterDuration(15));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17, duration.getEncounterDuration(16));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18, duration.getEncounterDuration(17));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19, duration.getEncounterDuration(18));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20, duration.getEncounterDuration(19));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21, duration.getEncounterDuration(20));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22, duration.getEncounterDuration(21));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23, duration.getEncounterDuration(22));
	    values.put(SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24, duration.getEncounterDuration(23));
		db.update(SQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, values, identifier, null);
	}
		
	/**
	 * This method updates a BTDevice already registered by the application. 
	 * This modifies the corresponding record to the BTDevice in the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @param averageDuration The Bluetooth device information regarding its average encounter duration.
	 */
	public void updateBTDevAvgEncounterDuration(UserDevAverageEncounterDuration averageDuration){
		String identifier = SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + averageDuration.getDevAdd() + "'";
		ContentValues values = new ContentValues();
		    
	    values.put(SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, averageDuration.getDevAdd());
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1, averageDuration.getAverageEncounterDuration(0));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2, averageDuration.getAverageEncounterDuration(1));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3, averageDuration.getAverageEncounterDuration(2));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4, averageDuration.getAverageEncounterDuration(3));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5, averageDuration.getAverageEncounterDuration(4));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6, averageDuration.getAverageEncounterDuration(5));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7, averageDuration.getAverageEncounterDuration(6));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8, averageDuration.getAverageEncounterDuration(7));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9, averageDuration.getAverageEncounterDuration(8));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10, averageDuration.getAverageEncounterDuration(9));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11, averageDuration.getAverageEncounterDuration(10));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12, averageDuration.getAverageEncounterDuration(11));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13, averageDuration.getAverageEncounterDuration(12));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14, averageDuration.getAverageEncounterDuration(13));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15, averageDuration.getAverageEncounterDuration(14));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16, averageDuration.getAverageEncounterDuration(15));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17, averageDuration.getAverageEncounterDuration(16));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18, averageDuration.getAverageEncounterDuration(17));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19, averageDuration.getAverageEncounterDuration(18));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20, averageDuration.getAverageEncounterDuration(19));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21, averageDuration.getAverageEncounterDuration(20));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22, averageDuration.getAverageEncounterDuration(21));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23, averageDuration.getAverageEncounterDuration(22));
	    values.put(SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24, averageDuration.getAverageEncounterDuration(23));
		db.update(SQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, values, identifier, null);
	}
		
	/**
	 * This method updates a BTDevice already registered by the application. 
	 * This modifies the corresponding record to the BTDevice in the BTDEVICESOCIALWEIGHT table.
	 * @param socialWeight The Bluetooth device information regarding its social weight.
	 */
	public void updateBTDevSocialWeight(UserDevSocialWeight socialWeight){
		String identifier = SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + socialWeight.getDevAdd() + "'";
		ContentValues values = new ContentValues();
	    
	    values.put(SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, socialWeight.getDevAdd());
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1, socialWeight.getSocialWeight(0));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2, socialWeight.getSocialWeight(1));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3, socialWeight.getSocialWeight(2));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4, socialWeight.getSocialWeight(3));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5, socialWeight.getSocialWeight(4));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6, socialWeight.getSocialWeight(5));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7, socialWeight.getSocialWeight(6));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8, socialWeight.getSocialWeight(7));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9, socialWeight.getSocialWeight(8));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10, socialWeight.getSocialWeight(9));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11, socialWeight.getSocialWeight(10));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12, socialWeight.getSocialWeight(11));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13, socialWeight.getSocialWeight(12));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14, socialWeight.getSocialWeight(13));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15, socialWeight.getSocialWeight(14));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16, socialWeight.getSocialWeight(15));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17, socialWeight.getSocialWeight(16));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18, socialWeight.getSocialWeight(17));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19, socialWeight.getSocialWeight(18));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20, socialWeight.getSocialWeight(19));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21, socialWeight.getSocialWeight(20));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22, socialWeight.getSocialWeight(21));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23, socialWeight.getSocialWeight(22));
	    values.put(SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24, socialWeight.getSocialWeight(23));
		db.update(SQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, values, identifier, null);
	}
		
	/**
	 * This method gets information about a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDev The btDev object, null if not found.
	 */
	public UserDeviceInfo getBTDevice(String btDevice) {
		UserDeviceInfo btDev;
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevice + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDev = cursorToBTDevice(cursor);
		else
			btDev = null;	
		cursor.close();
		return btDev;
	}
	
	/**
	 * This method gets encounter duration information of a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDevEncDur The btDevEncDur object, null if not found.
	 */
	public UserDevEncounterDuration getBTDeviceEncounterDuration(String btDevEncounterDuration){
		UserDevEncounterDuration btDevEncDur;
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, 
				allColumnsBTDeviceEncounterDuration, SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevEncounterDuration + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevEncDur = cursorToBTDevEncounterDuration(cursor);
		else
			btDevEncDur = null;	
		cursor.close();
		return btDevEncDur;
	}
		
	/**
	 * This method gets average encounter duration information of a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDevAvgEncDur The btDevAvgEncDur object, null if not found.
	 */
	public UserDevAverageEncounterDuration getBTDeviceAverageEncounterDuration(String btDevAverageEncounterDuration){
		UserDevAverageEncounterDuration btDevAvgEncDur;
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, 
				allColumnsBTDeviceAverageEncounterDuration, SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevAverageEncounterDuration + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevAvgEncDur = cursorToBTDevAverageEncounterDuration(cursor);
		else
			btDevAvgEncDur = null;	
		cursor.close();
		return btDevAvgEncDur;
	}
		
	/**
	 * This method gets social weight information of a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDevSocWeight The btDevSocWeight object, null if not found.
	 */
	public UserDevSocialWeight getBTDeviceSocialWeight(String btDevSocialWeight){
		UserDevSocialWeight btDevSocWeight;
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, 
				allColumnsBTDeviceSocialWeight, SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevSocialWeight + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevSocWeight = cursorToBTDevSocialWeight(cursor);
		else
			btDevSocWeight = null;	
		cursor.close();
		return btDevSocWeight;
	}
		
	/**
	 * This method checks if a given BTDevice has already been registered by the application.
	 * @param mac The MAC address of the BTDevice.
	 * @return true, if BTDevice has already been registered by the application, false otherwise.
	 */
	public boolean hasBTDevice (String btDev) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + SQLiteHelper.TABLE_BTDEVICE + " WHERE " + SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDev + "'", null) == 0)? false : true;
	}
	
	/**
	 * This method gets the all the BTDevice recorded by the application on the BTDEVICE table.
	 * @return btDevMap The map with the BTUserDevice objects, and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, UserDeviceInfo> getAllBTDevice() {
		Map<String, UserDeviceInfo> btDevMap = new TreeMap<String, UserDeviceInfo>();
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserDeviceInfo btDev = cursorToBTDevice(cursor);
			btDevMap.put(btDev.getDevAdd(), btDev);
			cursor.moveToNext();
		}
	    cursor.close();
	    return btDevMap;
	}
	
	/**
	 * This method gets the all the BTUserDevEncounterDuration recorded by the application 
	 * on the BTDEVICEENCOUNTERDURATION table.
	 * @return btDevEncounterDurationMap The map with the BTUserDevEncounterDuration objects, 
	 * and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, UserDevEncounterDuration> getAllBTDevEncounterDuration() {
		Map<String, UserDevEncounterDuration> btDevEncounterDurationMap = new TreeMap<String, UserDevEncounterDuration>();
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, allColumnsBTDeviceEncounterDuration, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserDevEncounterDuration btDevEncDur = cursorToBTDevEncounterDuration(cursor);
			btDevEncounterDurationMap.put(btDevEncDur.getDevAdd(), btDevEncDur);
			cursor.moveToNext();
		}
	    cursor.close();
	    return btDevEncounterDurationMap;
	}
		
	/**
	 * This method gets the all the BTUserDevAverageEncounterDuration recorded by the application 
	 * on the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return btDevAverageEncounterDurationMap The map with the BTUserDevAverageEncounterDuration objects, 
	 * and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, UserDevAverageEncounterDuration> getAllBTDevAverageEncounterDuration() {
		Map<String, UserDevAverageEncounterDuration> btDevAverageEncounterDurationMap = new TreeMap<String, UserDevAverageEncounterDuration>();
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, 
				allColumnsBTDeviceAverageEncounterDuration, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserDevAverageEncounterDuration btDevAvgEncDur = cursorToBTDevAverageEncounterDuration(cursor);
			btDevAverageEncounterDurationMap.put(btDevAvgEncDur.getDevAdd(), btDevAvgEncDur);
			cursor.moveToNext();
		}
	    cursor.close();
	    return btDevAverageEncounterDurationMap;
	}
		
	/**
	 * This method gets the all the BTUserDevSocialWeight recorded by the application 
	 * on the BTDEVICESOCIALWEIGHT table.
	 * @return btDevSocialWeightMap The map with the BTUserDevSocialWeight objects, 
	 * and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, UserDevSocialWeight> getAllBTDevSocialWeight() {
		Map<String, UserDevSocialWeight> btDevSocialWeightMap = new TreeMap<String, UserDevSocialWeight>();
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, 
				allColumnsBTDeviceSocialWeight, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserDevSocialWeight btDevSocWeight = cursorToBTDevSocialWeight(cursor);
			btDevSocialWeightMap.put(btDevSocWeight.getDevAdd(), btDevSocWeight);
			cursor.moveToNext();
		}
	    cursor.close();
	    return btDevSocialWeightMap;
	}
}


