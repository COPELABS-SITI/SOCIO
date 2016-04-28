package com.copelabs.oiframework.socialproximity;

import java.util.ArrayList;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application. 
 * It provides the interface between OnSocialWeightUpdate and SocialProximity.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

public interface DataBaseChangeListener {
	void onDataBaseChangeUserDevice(ArrayList<UserDeviceInfo> arrayList);
	void onDataBaseChangeEncDur(ArrayList<UserDevEncounterDuration> arrayList);
	void onDataBaseChangeAvgEncDur(ArrayList<UserDevAverageEncounterDuration> arrayList);
	void onDataBaseChangeSocialWeight(ArrayList<UserDevSocialWeight> arrayList);	

}