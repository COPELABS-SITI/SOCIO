package com.copelabs.oiframework.socialproximity;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application. 
 * It provides the interface between SocialProximity and Routing.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

public interface SocialProximityListener {
	void notifySWListUpdate();
	void notifyNewDeviceEntry();
}
