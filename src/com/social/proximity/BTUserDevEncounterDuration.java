/**
 *  BTUserDevEncounterDuration holds the encounter duration between a peer and the user device in specific time slots.
 *  The information kept are device MAC address and time spent in the vicinity (i.e., total time within communication range).
 *
 * @author Waldir Moreira (ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 */

package com.social.proximity;

public class BTUserDevEncounterDuration {
	private String deviceAdd;
	private double[] encounterDuration = new double [24];
	
	
	/**
     * Gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address to be used as key.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	
	/**
     * Gets the duration that the BT device is within communication range.
	 * @param timeSlot Specific time slot.
	 * @return encounterDuration The encounter duration in the given slot. 
	 */
	public double getEncounterDuration(int timeSlot) {
		return encounterDuration[timeSlot];
	}
	
	/**
	 * Sets the ID of this Bluetooth device.
	 * @param DevAdd the MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
     * Sets the duration that the BT device is within communication range.
	 * @param timeSlot Specific time slot.
	 * @param encounterDuration The duration of encounter.
	 */
	public void setEncounterDuration(int timeSlot, double encounterDuration) {
		this.encounterDuration[timeSlot] = encounterDuration;
	}

    /**
     * BT User Device Encounter Duration Constructor.
     */
	public BTUserDevEncounterDuration() {
		super();
	}

}
