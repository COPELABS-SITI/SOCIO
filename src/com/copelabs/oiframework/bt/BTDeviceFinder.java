package com.copelabs.oiframework.bt;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application. 
 * It provides the interface between BTManager and SocialProximity.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

public interface BTDeviceFinder {
	void onDeviceFound(BluetoothDevice device, BluetoothClass btClass);
}
