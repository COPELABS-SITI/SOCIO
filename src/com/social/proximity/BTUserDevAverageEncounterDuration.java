/**
 *  BTUserDevAverageEncounterDuration holds the average duration of encounter between a peer and the user device in specific time slots.
 *  The information kept are device MAC address and average encounter duration.
 *
 * @author Waldir Moreira (ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 */

package com.social.proximity;

public class BTUserDevAverageEncounterDuration {
	private String deviceAdd;
	private double[] averageEncounterDuration = new double [24];
	
	
	/**
     * Gets the MAC address of this Bluetooth device.
	 * @return deviceAdd the device address to be used as key.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	
	/**
     * Gets the average encounter duration that the BT device is within communication range.
	 * @param timeSlot Specific time slot.
	 * @return averageEncounterDuration The average encounter duration in the given slot. 
	 */
	public double getAverageEncounterDuration(int timeSlot) {
		return averageEncounterDuration[timeSlot];
	}
	
	/**
	 * Sets the ID of this Bluetooth device.
	 * @param DevAdd the MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
     * Sets the average duration that the BT device is within communication range.
	 * @param timeSlot Specific time slot.
	 * @param avgEncounterDuration The average duration of encounter.
	 */
	public void setAverageEncounterDuration(int timeSlot, double avgEncounterDuration) {
		this.averageEncounterDuration[timeSlot] = avgEncounterDuration;
	}

    /**
     * BT User Device Average Encounter Duration Constructor.
     */
	public BTUserDevAverageEncounterDuration() {
		super();
	}

}
