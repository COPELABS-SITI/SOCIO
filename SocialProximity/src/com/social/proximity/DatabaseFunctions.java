/**
 * DatabaseFunctions provides all the functions to handle 
 * the necessary tables for social proximity computations in the provided database.
 *
 * @author Waldir Moreira (COPELABS/ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseFunctions {
	
	private SQLiteDatabase db;
	
	/**
	 * DatabaseFunctions constructor.
	 * @param database The database provided by the application.
	 */
	public DatabaseFunctions (SQLiteDatabase database) {
		db = database;		
	}
	
	//Bluetooth pipeline
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
			SQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART,
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
			SQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24,
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
			SQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24,
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
			SQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24,
	};
	
	/**
	 * Converts a cursor pointing to a record in the BTDEVICE table to a BTDevice object.
	 * @param cursor Cursor pointing to a record of the BTDEVICE table.
	 * @return the BTDevice object.
	 */
	private BTUserDevice cursorToBTDevice(Cursor cursor) {
		BTUserDevice btDev = new BTUserDevice();
		
		btDev.setDevAdd(cursor.getString(0));
		btDev.setDevName(cursor.getString(1));
		btDev.setEncounterTime(cursor.getLong(2));
		
		return btDev;
	}

	/**
	 * Converts a cursor pointing to a record in the BTDEVICEENCOUNTERDURATION table to a BTUserDevEncounterDuration object.
	 * @param cursor Cursor pointing to a record of the BTDEVICEENCOUNTERDURATION table.
	 * @return the BTUserDevEncounterDuration object.
	 */
	private BTUserDevEncounterDuration cursorToBTDevEncounterDuration(Cursor cursor) {
		BTUserDevEncounterDuration duration = new BTUserDevEncounterDuration();
		
		duration.setDevAdd(cursor.getString(0));
		
		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			duration.setEncounterDuration(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return duration;
	}
	
	/**
	 * Converts a cursor pointing to a record in the BTDEVICEAVERAGEENCOUNTERDURATION table to a BTUserDevAverageEncounterDuration object.
	 * @param cursor Cursor pointing to a record of the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return the BTUserDevAverageEncounterDuration object.
	 */
	private BTUserDevAverageEncounterDuration cursorToBTDevAverageEncounterDuration(Cursor cursor) {
		BTUserDevAverageEncounterDuration averageDuration = new BTUserDevAverageEncounterDuration();
		
		averageDuration.setDevAdd(cursor.getString(0));
		
		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			averageDuration.setAverageEncounterDuration(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return averageDuration;
	}
	
	/**
	 * Converts a cursor pointing to a record in the BTDEVICESOCIALWEIGHT table to a BTUserDevSocialWeight object.
	 * @param cursor Cursor pointing to a record of the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return the BTUserDevSocialWeight object.
	 */
	private BTUserDevSocialWeight cursorToBTDevSocialWeight(Cursor cursor) {
		BTUserDevSocialWeight socialWeight = new BTUserDevSocialWeight();
		
		socialWeight.setDevAdd(cursor.getString(0));
		
		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			socialWeight.setSocialWeight(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return socialWeight;
	}

	/**
	 * Gets the number of records in the BTDEVICE table. This is, the number of BTDevice registered on the application.
	 * @return the number of BTDevice registered by the application.
	 */
	public long getNumBTDevice(){
		return DatabaseUtils.queryNumEntries(db, SQLiteHelper.TABLE_BTDEVICE);
	}
	
	/**
	 * Register a new BTDevice in the application. 
	 * It creates a new record on the BTDEVICE, BTDEVICEENCOUNTERDURATION, BTDEVICEAVERAGEENCOUNTERDURATION, and BTDEVICESOCIALWEIGHT tables,
	 * with the information passed as BTDevice.
	 * @param btDev Bluetooth device information.
	 * @param duration Bluetooth device information regarding the duration that the BT device is within communication range of others.
	 * @param averageDuration Bluetooth device information regarding the average duration of encounter between the BT device and other devices.
	 * @param socialWeight Bluetooth device information regarding the social weight of the BT device towards others.
	 */
	public void registerNewBTDevice (BTUserDevice btDev, BTUserDevEncounterDuration duration, 
			BTUserDevAverageEncounterDuration averageDuration, BTUserDevSocialWeight socialWeight) {
		
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
	}
	
	/**
	 * Update a BTDevice already registered by the application. This modifies the corresponding record to the BTDevice in the 
	 * BTDEVICE, BTDEVICEENCOUNTERDURATION, BTDEVICEAVERAGEENCOUNTERDURATION, and BTDEVICESOCIALWEIGHT tables.
	 * @param btDev Bluetooth device information.
	 * @param duration Bluetooth device information regarding the duration that the BT device is within communication range of others.
	 */
	public void updateBTDeviceAndDuration(BTUserDevice btDev, BTUserDevEncounterDuration duration){
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
	 * Update a BTDevice already registered by the application. This modifies the corresponding record to the BTDevice in the 
	 *  BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @param averageDuration Bluetooth device information regarding its average encounter duration.
	 */
	public void updateBTDevAvgEncounterDuration(BTUserDevAverageEncounterDuration averageDuration){
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
	 * Update a BTDevice already registered by the application. This modifies the corresponding record to the BTDevice in the 
	 * BTDEVICESOCIALWEIGHT table.
	 * @param socialWeight Bluetooth device information regarding its social weight.
	 */
	public void updateBTDevSocialWeight(BTUserDevSocialWeight socialWeight){
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
	 * Gets information about a BTDevice already registered by the application. 
	 * @param btDevice The MAC address of the BTDevice which information should be returned.
	 * @return the btDev object, null if not found.
	 */
	public BTUserDevice getBTDevice(String btDevice) {
		BTUserDevice btDev;
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevice + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDev = cursorToBTDevice(cursor);
		else
			btDev = null;	
		
		cursor.close();
		return btDev;
	}
	
	/**
	 * Gets encounter duration information of a BTDevice already registered by the application. 
	 * @param btDevEncounterDuration The MAC address of the BTDevice which information should be returned.
	 * @return the btDevEncDur object, null if not found.
	 */
	public BTUserDevEncounterDuration getBTDeviceEncounterDuration(String btDevEncounterDuration){
		BTUserDevEncounterDuration btDevEncDur;
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
	 * Gets average encounter duration information of a BTDevice already registered by the application. 
	 * @param btDevAverageEncounterDuration The MAC address of the BTDevice which information should be returned.
	 * @return the btDevAvgEncDur object, null if not found.
	 */
	public BTUserDevAverageEncounterDuration getBTDeviceAverageEncounterDuration(String btDevAverageEncounterDuration){
		BTUserDevAverageEncounterDuration btDevAvgEncDur;
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
	 * Gets social weight information of a BTDevice already registered by the application. 
	 * @param btDevSocialWeight The MAC address of the BTDevice which information should be returned.
	 * @return the btDevSocWeight object, null if not found.
	 */
	public BTUserDevSocialWeight getBTDeviceSocialWeight(String btDevSocialWeight){
		BTUserDevSocialWeight btDevSocWeight;
		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, 
				allColumnsBTDeviceAverageEncounterDuration, SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevSocialWeight + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevSocWeight = cursorToBTDevSocialWeight(cursor);
		else
			btDevSocWeight = null;	
		
		cursor.close();
		return btDevSocWeight;
	}
	
	/**
	 * Checks if a given BTDevice has already been registered by the application.
	 * @param btDev The MAC address of the BTDevice.
	 * @return true, if BTDevice has already been registered by the application, false otherwise.
	 */
	public boolean hasBTDevice (String btDev) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + SQLiteHelper.TABLE_BTDEVICE + " WHERE " + SQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDev + "'", null) == 0)? false : true;
	}
	
	/**
	 * Gets the all the BTDevice recorded by the application on the BTDEVICE table.
	 * @return btDevMap A map with the BTUserDevice objects, and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevice> getAllBTDevice() {
		Map<String, BTUserDevice> btDevMap = new TreeMap<String, BTUserDevice>();

		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			BTUserDevice btDev = cursorToBTDevice(cursor);
			btDevMap.put(btDev.getDevAdd(), btDev);
			cursor.moveToNext();
		}

	    cursor.close();
	    return btDevMap;
	}

	/**
	 * Gets the all the BTUserDevEncounterDuration recorded by the application on the BTDEVICEENCOUNTERDURATION table.
	 * @return btDevEncounterDurationMap A map with the BTUserDevEncounterDuration objects, and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevEncounterDuration> getAllBTDevEncounterDuration() {
		Map<String, BTUserDevEncounterDuration> btDevEncounterDurationMap = new TreeMap<String, BTUserDevEncounterDuration>();

		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, allColumnsBTDeviceEncounterDuration, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			BTUserDevEncounterDuration btDevEncDur = cursorToBTDevEncounterDuration(cursor);
			btDevEncounterDurationMap.put(btDevEncDur.getDevAdd(), btDevEncDur);
			cursor.moveToNext();
		}

	    cursor.close();
	    return btDevEncounterDurationMap;
	}
	
	/**
	 * Gets the all the BTUserDevAverageEncounterDuration recorded by the application on the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return btDevAverageEncounterDurationMap A map with the BTUserDevAverageEncounterDuration objects, and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevAverageEncounterDuration> getAllBTDevAverageEncounterDuration() {
		Map<String, BTUserDevAverageEncounterDuration> btDevAverageEncounterDurationMap = new TreeMap<String, BTUserDevAverageEncounterDuration>();

		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, 
				allColumnsBTDeviceAverageEncounterDuration, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			BTUserDevAverageEncounterDuration btDevAvgEncDur = cursorToBTDevAverageEncounterDuration(cursor);
			btDevAverageEncounterDurationMap.put(btDevAvgEncDur.getDevAdd(), btDevAvgEncDur);
			cursor.moveToNext();
		}

	    cursor.close();
	    return btDevAverageEncounterDurationMap;
	}
	
	/**
	 * Gets the all the BTUserDevSocialWeight recorded by the application on the BTDEVICESOCIALWEIGHT table.
	 * @return btDevSocialWeightMap A map with the BTUserDevSocialWeight objects, and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevSocialWeight> getAllBTDevSocialWeight() {
		Map<String, BTUserDevSocialWeight> btDevSocialWeightMap = new TreeMap<String, BTUserDevSocialWeight>();

		Cursor cursor = db.query(SQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, 
				allColumnsBTDeviceSocialWeight, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			BTUserDevSocialWeight btDevSocWeight = cursorToBTDevSocialWeight(cursor);
			btDevSocialWeightMap.put(btDevSocWeight.getDevAdd(), btDevSocWeight);
			cursor.moveToNext();
		}

	    cursor.close();
	    return btDevSocialWeightMap;
	}

}
