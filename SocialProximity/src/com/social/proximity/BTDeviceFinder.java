/**
 * BTDeviceFinder provides the interface between BTManager and SocialProximity.
 *
 * @author Waldir Moreira (COPELABS/ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public interface BTDeviceFinder {
	/**
	 * Creates the entries for a specifc peer in the database.
	 * @param device The Bluetooth device found and respective information (MAC address, name, among others).
	 * @param btClass The characteristics and capabilities of the found Bluetooth device.
	 */
	void onDeviceFound(BluetoothDevice device, BluetoothClass btClass);
}
