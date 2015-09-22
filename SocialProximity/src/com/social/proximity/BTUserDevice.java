/**
 *  BTUserDevice represents a user device found by means of Bluetooth.
 *  The information kept are device name, MAC address, and time of first encounter.
 *
 * @author Waldir Moreira (ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 */

package com.social.proximity;

public class BTUserDevice {
	private String deviceAdd;
	private String deviceName;
	private long encounterTime;
	
	/**
     * Gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	/**
     * Gets the name of this Bluetooth device.
	 * @return deviceName The device name.
	 */
	public String getDevName() {
		return deviceName;
	}

	/**
     * Gets the time that the BT device is is first found.
	 * @return encounterTime The start time of encounter.
	 */
	public long getEncounterStart() {
		return encounterTime;
	}
	
	/**
	 * Sets the ID of this Bluetooth device.
	 * @param DevAdd The MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
	 * Sets the name of this Bluetooth device.
	 * @param DevName The name of device to set.
	 */
	public void setDevName(String DevName) {
		deviceName = DevName;
	}
	
	/**
     * Sets the time that the BT device is first found.
	 * @param time The time of first encounter.
	 */
	public void setEncounterTime(long time) {
		this.encounterTime = time;
	}

    /**
     * Bluetooth User Device Constructor.
     */
	public BTUserDevice() {
		super();
	}
}



