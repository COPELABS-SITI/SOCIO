/**
 * DataBaseChangeListener provides the interface between OnNewHourUpdate and 
 * BTUserDevice, BTUserDevEncounterDuration, BTUserDevAverageEncounterDuration,
 * and BTUserDevSocialWeight classes. 
 *
 * @author Waldir Moreira (COPELABS/ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import java.util.ArrayList;

public interface DataBaseChangeListener {
	/**
	 * Reports on changes on the Bluetooth device information.
	 * @param arrayList The list containing the peer information (MAC address, device's name, and time of first encounter).
	 */
	void onDataBaseChangeBT(ArrayList<BTUserDevice> arrayList);
	/**
	 * Reports on changes on the Bluetooth device information.
	 * @param arrayList The list containing the peer information (MAC address and time spent in the vicinity, i.e., total time within communication range).
	 */
	void onDataBaseChangeBTEncDur(ArrayList<BTUserDevEncounterDuration> arrayList);
	/**
	 * Reports on changes on the Bluetooth device information.
	 * @param arrayList The list containing the peer information (MAC address and average encounter duration).
	 */
	void onDataBaseChangeBTAvgEncDur(ArrayList<BTUserDevAverageEncounterDuration> arrayList);
	/**
	 * Reports on changes on the Bluetooth device information.
	 * @param arrayList The list containing the peer information (MAC address and social weight).
	 */
	void onDataBaseChangeBTSocialWeight(ArrayList<BTUserDevSocialWeight> arrayList);	

}