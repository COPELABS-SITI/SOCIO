/**
 * SQLiteHelper provides all the functions to create the necessary tables 
 * for social proximity computations in the provided database.
 *
 * @author Waldir Moreira (COPELABS/ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{
     
	private static final String DATABASE_NAME = "database";
	private static final int DATABASE_VERSION = 2;

	//Bluetooth pipeline
	public static final String TABLE_BTDEVICE = "btdevices";
	public static final String TABLE_BTDEVICEENCOUNTERDURATION = "btdevice_encounterduration";
	public static final String TABLE_BTDEVICEAVERAGEENCOUNTERDURATION = "btdevice_averageencounterduration";
	public static final String TABLE_BTDEVICESOCIALWEIGHT = "btdevice_socialweight";

	public static final String COLUMN_ID = "_id";

	//Bluetooth pipeline
	public static final String COLUMN_BTDEV_MAC_ADDRESS = "devBtMacAdd";
	public static final String COLUMN_BTDEV_NAME = "devName";
	public static final String COLUMN_BTDEV_ENCOUNTERSTART = "devEncounterStart";
	//	Encounter Duration
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1= "devEncounterDuration_slot1";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2= "devEncounterDuration_slot2";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3= "devEncounterDuration_slot3";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4= "devEncounterDuration_slot4";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5= "devEncounterDuration_slot5";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6= "devEncounterDuration_slot6";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7= "devEncounterDuration_slot7";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8= "devEncounterDuration_slot8";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9= "devEncounterDuration_slot9";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10= "devEncounterDuration_slot10";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11= "devEncounterDuration_slot11";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12= "devEncounterDuration_slot12";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13= "devEncounterDuration_slot13";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14= "devEncounterDuration_slot14";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15= "devEncounterDuration_slot15";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16= "devEncounterDuration_slot16";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17= "devEncounterDuration_slot17";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18= "devEncounterDuration_slot18";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19= "devEncounterDuration_slot19";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20= "devEncounterDuration_slot20";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21= "devEncounterDuration_slot21";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22= "devEncounterDuration_slot22";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23= "devEncounterDuration_slot23";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24= "devEncounterDuration_slot24";
	// Average Encounter Duration
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1= "devAvgEncounterDuration_slot1";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2= "devAvgEncounterDuration_slot2";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3= "devAvgEncounterDuration_slot3";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4= "devAvgEncounterDuration_slot4";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5= "devAvgEncounterDuration_slot5";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6= "devAvgEncounterDuration_slot6";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7= "devAvgEncounterDuration_slot7";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8= "devAvgEncounterDuration_slot8";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9= "devAvgEncounterDuration_slot9";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10= "devAvgEncounterDuration_slot10";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11= "devAvgEncounterDuration_slot11";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12= "devAvgEncounterDuration_slot12";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13= "devAvgEncounterDuration_slot13";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14= "devAvgEncounterDuration_slot14";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15= "devAvgEncounterDuration_slot15";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16= "devAvgEncounterDuration_slot16";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17= "devAvgEncounterDuration_slot17";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18= "devAvgEncounterDuration_slot18";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19= "devAvgEncounterDuration_slot19";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20= "devAvgEncounterDuration_slot20";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21= "devAvgEncounterDuration_slot21";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22= "devAvgEncounterDuration_slot22";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23= "devAvgEncounterDuration_slot23";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24= "devAvgEncounterDuration_slot24";
	// Social Weight
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT1= "devSocialWeight_slot1";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT2= "devSocialWeight_slot2";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT3= "devSocialWeight_slot3";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT4= "devSocialWeight_slot4";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT5= "devSocialWeight_slot5";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT6= "devSocialWeight_slot6";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT7= "devSocialWeight_slot7";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT8= "devSocialWeight_slot8";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT9= "devSocialWeight_slot9";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT10= "devSocialWeight_slot10";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT11= "devSocialWeight_slot11";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT12= "devSocialWeight_slot12";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT13= "devSocialWeight_slot13";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT14= "devSocialWeight_slot14";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT15= "devSocialWeight_slot15";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT16= "devSocialWeight_slot16";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT17= "devSocialWeight_slot17";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT18= "devSocialWeight_slot18";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT19= "devSocialWeight_slot19";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT20= "devSocialWeight_slot20";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT21= "devSocialWeight_slot21";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT22= "devSocialWeight_slot22";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT23= "devSocialWeight_slot23";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT24= "devSocialWeight_slot24";

	//Bluetooth pipeline
	private static final String CREATE_BTDEVICE_TABLE = "create table "
		      + TABLE_BTDEVICE + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
		      + COLUMN_BTDEV_NAME + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERSTART + " text not null"
//		      + COLUMN_BTDEV_TIMEVICINITY + " text not null"
		      + ");";
	
	private static final String CREATE_BTDEVICEENCOUNTERDURATION_TABLE = "create table "
		      + TABLE_BTDEVICEENCOUNTERDURATION + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23 + " text not null, "
		      + COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24 + " text not null"
		      + ");";
	
	private static final String CREATE_BTDEVICEAVERAGEENCOUNTERDURATION_TABLE = "create table "
		      + TABLE_BTDEVICEAVERAGEENCOUNTERDURATION + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23 + " text not null, "
		      + COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24 + " text not null"
		      + ");";
	
	private static final String CREATE_BTDEVICESOCIALWEIGHT_TABLE = "create table "
		      + TABLE_BTDEVICESOCIALWEIGHT + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT1 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT2 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT3 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT4 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT5 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT6 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT7 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT8 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT9 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT10 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT11 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT12 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT13 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT14 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT15 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT16 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT17 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT18 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT19 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT20 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT21 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT22 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT23 + " text not null, "
		      + COLUMN_BTDEV_SOCIALWEIGHT_SLOT24 + " text not null"
		      + ");";
	
	/**
	 * SQLiteHelper constructor.
	 * @param context The context which provides access to resources and specific classes of the application.
	 */
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	public void onCreate(SQLiteDatabase dataBase)
	{
		//Bluetooth pipeline
		dataBase.execSQL(CREATE_BTDEVICE_TABLE);
		dataBase.execSQL(CREATE_BTDEVICEENCOUNTERDURATION_TABLE);
		dataBase.execSQL(CREATE_BTDEVICEAVERAGEENCOUNTERDURATION_TABLE);
		dataBase.execSQL(CREATE_BTDEVICESOCIALWEIGHT_TABLE);
	}

	public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion)
	{
		//Bluetooth pipeline
    	dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICE);
    	dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICEENCOUNTERDURATION);
    	dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICEAVERAGEENCOUNTERDURATION);
    	dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICESOCIALWEIGHT);
		onCreate(dataBase);
	}


}
