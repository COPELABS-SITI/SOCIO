package com.copelabs.oiframework.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.copelabs.oiaidllibrary.UserDevice;
import com.copelabs.oiframework.contentmanager.Packet;
import com.copelabs.oiframework.socialproximity.SocialProximity;
import com.copelabs.oiframework.socialproximity.SocialProximityListener;
import com.copelabs.oiframework.wifi.WiFiDirect;
import com.copelabs.oiframework.wifi.WiFiDirectDevice;
import com.copelabs.oiframework.wifi.WiFiDirectListener;
import com.copelabs.oiframework.wifi.WiFiDirectUtils;

import android.content.Context;
import android.util.Log;

public class Routing {
	
	public static final String TAG="Routing";
	
	public ArrayList<RoutingListener> listeners = new ArrayList<RoutingListener> ();
	
	private SocialProximity pSocialProximity;
	private WiFiDirect pWiFiDirect;
	

	public Routing(Context context) {
		pSocialProximity = new SocialProximity(context);
		pSocialProximity.setSocialProximityListener(new SocialProximityListener() {

			@Override
			public void notifySWListUpdate() {
				//Log.i(TAG, "*****ROUTING KNOW ABOUT UPDATE SOCIAL WEIGHT LIST *** ");
				announceListSocialWeight(getAllSWList());
			}

			@Override
			public void notifyNewDeviceEntry() {
				notifyContactListUpdated(pSocialProximity.getContactList());
			}

		});
		
		pWiFiDirect = new WiFiDirect(context);
		pWiFiDirect.setWiFiDirectListener(new WiFiDirectListener() {

			@Override
			public void onPacketReceived(List<Packet> mListOfPackets) {
				notifyPacketReceived(mListOfPackets);
			}

			@Override
			public void onNewWiFiDirectDeviceFound(WiFiDirectDevice mDevice, Map<String, String> mListOfSocialWeight) {
				
				if (pSocialProximity == null)
					return;
				
				UserDevice mUserDevice = new UserDevice(mDevice.getDevice().deviceAddress, mDevice.getDevice().deviceName);	
				Log.i(TAG, "Device Found " + mDevice.getDevice().deviceAddress + " " + mDevice.getDevice().deviceName);
				
				List<UserDevice> mList = new ArrayList<UserDevice>(); 
				
				if (!mListOfSocialWeight.containsKey(WiFiDirectUtils.KEY_MAC)) {
					Log.e(TAG, mDevice.getDevice().deviceName + " Invalid info received");
					return;
				}
				notifyNewDeviceFound(mUserDevice);
				
				//Add device found to the list
				UserDevice mDeviceFound = new UserDevice(mListOfSocialWeight.get(WiFiDirectUtils.KEY_MAC), null);
				mList.add(mDeviceFound);
				mListOfSocialWeight.remove(WiFiDirectUtils.KEY_MAC);
				//Add devices from the social weight list
				for (Entry<String,String> entry : mListOfSocialWeight.entrySet()) {
					//Take entry string and add colons
					Log.i(TAG, "ListSW Received: " + addColonMAC(entry.getKey()) + " SW: " + entry.getValue());
					UserDevice mDeviceFromList = new UserDevice(addColonMAC(entry.getKey()), null);
					mList.add(mDeviceFromList);
				}
				
				Map<String, List<Packet>> mPacketsAvailableToSend = getListOfPackets(mList);
				if (mPacketsAvailableToSend == null) {
					Log.i(TAG, "No Messages to Send are availble");
					return;
				}
				if (mPacketsAvailableToSend.isEmpty()) {
					Log.i(TAG, "No Messages to Send are availble");
					return;
				}

				//TODO: Put this routing mechanism inside a method
				// Routing mechanism
				ArrayList<String> mDevicesToCheckSocialWeight = new ArrayList<String>(mPacketsAvailableToSend.keySet());
				Map<String, Integer> mMySWToDevices = getSWList(mDevicesToCheckSocialWeight); // From double to Int to reduce size of SW
				
				if (mMySWToDevices == null) {
					Log.w(WiFiDirect.TAG, "Local Social Weight empty");
					return;
				}				
				if (mMySWToDevices.isEmpty()) {
					Log.w(WiFiDirect.TAG, "Local Social Weight empty");
					return;
				}
				List<Packet> mPacketsToSend = new ArrayList<Packet>();
				for (Entry<String, List<Packet>> entry : mPacketsAvailableToSend.entrySet()) {
					
					Log.i(TAG, "entry.getKey(): " + entry.getKey());
					Log.i(TAG, "mDeviceFound.getDevAdd(): " + mDeviceFound.getDevAdd());
					
					//Check if Encounter peer is final destination
					if (entry.getKey().equalsIgnoreCase(mDeviceFound.getDevAdd())) {
						mPacketsToSend.addAll(entry.getValue());
						continue;
					}
					
					//Not the final destination
					try {
						// We need to treat entry.getKey() with replace(":", "").substring(6) as mListOfSocialWeight holds only the last 6 digits of MAC
						int mPeerSW = Integer.parseInt(mListOfSocialWeight.get(entry.getKey().replace(":", "").substring(6))); // From double to Int to reduce size of SW
						int mMySW = mMySWToDevices.get(entry.getKey()); // From double to Int to reduce size of SW

						//TODO: check if mMySW is null (no device found on my SW table)
						if(mPeerSW >= mMySW)
							mPacketsToSend.addAll(entry.getValue());
					} catch (NumberFormatException e) {
						Log.e(TAG, "Error getting Peer SW for " + entry.getKey());
						continue;
					}
				}
				Log.i(TAG, "Number of Packets available to send to the device found: " + mPacketsToSend.size());
				if (mPacketsToSend.size() > 0)
					pWiFiDirect.sendPackets(mDevice, (ArrayList<Packet>) mPacketsToSend);
			}

			@Override
			public void onWiFiDirectDeviceDisappear(WiFiDirectDevice mDevice) {
				UserDevice mUserDevice = new UserDevice(mDevice.getDevice().deviceAddress, mDevice.getDevice().deviceName);	
				notifyDeviceDisappear(mUserDevice);
			}

			@Override
			public void error(int mError) {
				notifyError(mError);
			}
		
		});
		//Start WiFi Direct package and starts broadcasting the Social Weight List. 
		pWiFiDirect.start(getLocalMacAddress(), convertMapValueDoubleToString(pSocialProximity.getAllSWList()));
	}
	
	//Used to complete the mac address with colons
	private String addColonMAC(String toConvert){
		String mMACwithColon = toConvert.substring(0, 2).concat(":").concat(toConvert.substring(2, 4)).concat(":").concat(toConvert.substring(4));
		return mMACwithColon;
	}
	
	// used to update broadcast information at the interface layer.
	private void announceListSocialWeight(Map<String, Integer> mList) { // From double to Int to reduce size of SW
		pWiFiDirect.updateAnnounce(getLocalMacAddress(), convertMapValueDoubleToString(mList));
	}
	
	private Map<String, String> convertMapValueDoubleToString(Map<String, Integer> map) { // From double to Int to reduce size of SW
		//Convert Map<String, Double> to Map<String, String> needed by the WiFi Direct
		Map<String, String> mListOfSocialWeight = new HashMap<String, String>();
		for (Map.Entry<String, Integer> e : map.entrySet()) { // From double to Int to reduce size of SW
		   mListOfSocialWeight.put(e.getKey().replace(":", "").substring(6), e.getValue().toString()); //reduce MAC Address
		}
		return mListOfSocialWeight;
	}
	
	/**
	 * Provides a list of contact based on the information of devices encountered
	 * @return The list of contacts or null
	 */
	public List<UserDevice> getContactList() {
		if(pSocialProximity == null)
			return null;
		return pSocialProximity.getContactList();
	}
	
	/**
	 * Provides a list of encountered devices and social weight towards them 
	 * @return The list of encountered devices and social weight towards them, or null
	 */
	public Map<String, Integer> getSWList(ArrayList<String> mDevices) { // From double to Int to reduce size of SW
		if(pSocialProximity == null)
			return null;
		return pSocialProximity.getSWList(mDevices);
	}
	
	/**
	 * Provides a list of all encountered devices and social weight towards them 
	 * @return The list of all encountered devices and social weight towards them, or null
	 */
	public Map<String, Integer> getAllSWList() { // From double to Int to reduce size of SW
		if(pSocialProximity == null)
			return null;
		return pSocialProximity.getAllSWList();
	}
	
    /**
     * Provides the MAC Address of local Bluetooth adapter
     * @return localMacAdd The local MAC Address or null
     */
    public List<String> getLocalInfo(){
    	if(pSocialProximity == null)
			return null;
    	return pSocialProximity.getLocalInfo();
		
    }
    
    public String getLocalMacAddress(){
    	if(pSocialProximity == null)
			return null;
    	return pSocialProximity.getLocalInfo().get(1);
		
    }
    
    public String getLocalName(){
    	if(pSocialProximity == null)
			return null;
    	return pSocialProximity.getLocalInfo().get(0);
		
    }
	
	public void stop() {
		if (pWiFiDirect != null)
			pWiFiDirect.stop();
		if (pSocialProximity != null)
			pSocialProximity.stop();
	}
	
	
	public void setRoutingListener (RoutingListener listener) {
        this.listeners.add(listener);
    }
	
	public void newContentAvailable(Packet mPacket) {
		if (pWiFiDirect == null)
			return;
		Log.i(TAG, "Requesting SW List from neightbours...");
		pWiFiDirect.resetAvailableDevices();
	}
	
// LISTENERS FUNCTIONS ***************************************************************************
    
    /**
     * Notifies the ContentManager class that a new packet arrived.
     * @param mPacket Packet arrived 
     */
    private void notifyPacketReceived (List<Packet> mListOfPackets) {
    	for (RoutingListener listener : listeners) 
    	{
    	    listener.onPacketReceived(mListOfPackets);
    	}
    }

    /**
     * Provides a list of available packets to be sent to the given peers 
     * @return List of Packets available to send or null if Content Manager is not available.
     */
    private Map<String, List<Packet>> getListOfPackets (List<UserDevice> mListOfUserDevices) {
    	for (RoutingListener listener : listeners) 
    	{
    		return listener.getListOfPackets(mListOfUserDevices);
    	}
    	return null;
    }
    
    private void notifyNewDeviceFound (UserDevice mUserDevice) {
    	for (RoutingListener listener : listeners) 
    	{
    		listener.newDeviceFound(mUserDevice);
    	}
    }
    
    private void notifyDeviceDisappear (UserDevice mUserDevice) {
    	for (RoutingListener listener : listeners) 
    	{
    		listener.deviceLost(mUserDevice);
    	}
    }
    
    private void notifyContactListUpdated(List<UserDevice> mList) {
    	for (RoutingListener listener : listeners) 
    	{
    		listener.contactListUpdated(mList);
    	}
    }
    
    private void notifyError(int mError) {
    	for (RoutingListener listener : listeners) 
    	{
    		listener.error(mError);
    	}
    }
}
