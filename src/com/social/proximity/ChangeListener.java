/**
 * ChangeListener provides the interface between Service and SocialWeight classes.
 *
 * @author Waldir Moreira (COPELABS/ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import java.util.ArrayList;

public interface ChangeListener {
	/**
	 * Reports on changes on the social weights.
	 * @param arrayList The list containing the peer information (MAC address, device's name, and social weight). 
	 */
	void onSocialWeightChange(ArrayList<SocialWeight> arrayList);
}