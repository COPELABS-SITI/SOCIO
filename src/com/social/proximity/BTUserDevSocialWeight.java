/**
 *  BTUserDevSocialWeight holds the social weight between a peer and the user device in specific time slots.
 *  The information kept are device MAC address and social weight.
 *
 * @author Waldir Moreira (ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 */

package com.social.proximity;

public class BTUserDevSocialWeight {
	private String deviceAdd;
	private double[] socialWeight = new double [24];
	
	
	/**
     * Gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address to be used as key.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	
	/**
     * Gets the social weight towards a BT device.
	 * @param timeSlot Specific time slot.
	 * @return socialWeight The social weight in the given slot.
	 */
	public double getSocialWeight(int timeSlot) {
		return socialWeight[timeSlot];
	}
	
	/**
	 * Sets the ID of this Bluetooth device.
	 * @param DevAdd the MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
     * Sets the social weight towards a BT device.
	 * @param timeSlot Specific time slot.
	 * @param socialWeight The social weight.
	 */
	public void setSocialWeight(int timeSlot, double socialWeight) {
		this.socialWeight[timeSlot] = socialWeight;
	}

    /**
     * BT User Device Social Weight Constructor.
     */
	public BTUserDevSocialWeight() {
		super();
	}

}
