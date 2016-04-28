package com.copelabs.oiframework.wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.copelabs.oiframework.contentmanager.Packet;
import com.copelabs.oiframework.router.RoutingListener;

import android.content.Context;
import android.util.Log;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WiFiDirect {
    public static final String TAG = "wifidirect";
    
    public ArrayList<WiFiDirectListener> listeners = new ArrayList<WiFiDirectListener> ();
        
    private WiFiDirectUtils mWiFiDirectUtils;
   

    public WiFiDirect(Context mContext) {
    	mWiFiDirectUtils = new WiFiDirectUtils(mContext, this);
    }
    
    public void start(String mMAC, Map<String, String> mListOfSocialWeight) {
    	mWiFiDirectUtils.start(mMAC, mListOfSocialWeight);
    }

    public void stop() {
    	if (mWiFiDirectUtils != null)
    		mWiFiDirectUtils.stop();
    }
        
    //TODO: ALL INPUTS AND OUTPUTS FROM THIS MODULE MUST BE DEFINED HERE.
    
    /**
     * Listener that WiFi package will use to inform register packages 
     * about a new packet received or when new devices were found.
     * Check class WiFiDirectListener to see the available outputs.
     * @param listener listener to register
     */
    public void setWiFiDirectListener (WiFiDirectListener listener) 
    {
        this.listeners.add(listener);
    }
    
    /** 
     * Get the MAC address of WiFi P2P interface.
     * Only returns a MAC address if this device is a group owner.
     * @return WiFi P2P interface MAC address
     */
    public String getWiFiDirectMacAdress() {
    	return mWiFiDirectUtils.getWFDirectMacAddress();
    }
    
    /**
     * Get the MAC address of WiFi interface.
     * @return WiFi MAC address
     */
    public String getWiFiMacAddress(){
    	return mWiFiDirectUtils.getWiFiMacAddress();
    }
        
    public boolean sendPackets(WiFiDirectDevice mDevice, ArrayList<Packet> mPackets) {
    	Log.i(TAG, "Sending packets to " + mDevice.getDevice().deviceName);
    	mWiFiDirectUtils.makeConnection(mDevice, mPackets);
    	return true;
    }
    
    public void updateAnnounce(String mMAC, Map<String, String> mListOfSocialWeight) {
    	mWiFiDirectUtils.updateRegistration(mMAC, mListOfSocialWeight);
    }
    
    public void resetAvailableDevices() {
    	mWiFiDirectUtils.mAvailableDevices.clear();
    	mWiFiDirectUtils.restartDiscovery();
    }
   
}
