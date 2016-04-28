package com.copelabs.oiframework.socialproximity;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application. 
 * This class represents a user device found by means of Bluetooth.
 * The information kept are device name, MAC address, time of first encounter, time spent in the vicinity (i.e., total time within communication range).
 * @author Waldir Moreira (COPELABS/ULHT)
 */
public class UserDeviceInfo {
	private String deviceAdd;
	private String deviceName;
	private long encounterTime;
	
	/**
     * This method gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	/**
     * This method gets the name of this Bluetooth device.
	 * @return deviceName The device name.
	 */
	public String getDevName() {
		return deviceName;
	}

	/**
     * This method gets the time that the BT device is is first found.
	 * @param encounterTime The time.
	 */
	public long getEncounterStart() {
		return encounterTime;
	}
	
	/**
	 * This method sets the ID of this Bluetooth device.
	 * @param DevAdd The MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
	 * This method sets the name of this Bluetooth device.
	 * @param DevName The name of device to set.
	 */
	public void setDevName(String DevName) {
		deviceName = DevName;
	}
	
	/**
     * This method sets the time that the BT device is first found.
	 * @param time The time.
	 */
	public void setEncounterTime(long time) {
		this.encounterTime = time;
	}
	
    /**
     * Bluetooth User Device Constructor
     */
	public UserDeviceInfo() {
		super();
	}
}



