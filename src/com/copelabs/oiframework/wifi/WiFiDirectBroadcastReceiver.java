package com.copelabs.oiframework.wifi;

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private Channel channel;
    private WiFiDirectUtils mWiFiDirectUtils;
    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
    		WiFiDirectUtils mWiFiDirectUtils) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.mWiFiDirectUtils = mWiFiDirectUtils;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(WiFiDirect.TAG, action);
        
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        	// Check if WiFi is enabled or disabled
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
            	mWiFiDirectUtils.setIsWifiP2pEnabled(true);
            	mWiFiDirectUtils.WiFiStatus = mWiFiDirectUtils.WIFI_DISCONNECTED;
            	mWiFiDirectUtils.wifiWasEnabled();
            } else {
                mWiFiDirectUtils.setIsWifiP2pEnabled(false);
                mWiFiDirectUtils.wifiDisabled();
            }
            Log.i(WiFiDirect.TAG, "P2P state changed - " + state);
            
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
            	if (mWiFiDirectUtils.WiFiStatus == mWiFiDirectUtils.WIFI_CONNECTED)
            		return;
            	mWiFiDirectUtils.WiFiStatus = mWiFiDirectUtils.WIFI_CONNECTED;
                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.i(WiFiDirect.TAG,
                        "Connected to p2p network. Requesting network details");
                manager.requestConnectionInfo(channel,
                        (ConnectionInfoListener) mWiFiDirectUtils);
            } else {
                // It's a disconnect
            	if (mWiFiDirectUtils.WiFiStatus == mWiFiDirectUtils.WIFI_CONNECTING) {
            		Log.w(WiFiDirect.TAG, "Failed to connect or connection was refused.");
            		mWiFiDirectUtils.dealWithConnectionEnded(true);
            	} else if (mWiFiDirectUtils.WiFiStatus == mWiFiDirectUtils.WIFI_CONNECTED) {
            		Log.i(WiFiDirect.TAG, "WiFi P2P disconnected");
            		mWiFiDirectUtils.dealWithConnectionEnded(false);
            	}
            	mWiFiDirectUtils.WiFiStatus = mWiFiDirectUtils.WIFI_DISCONNECTED;
            }
            
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                .equals(action)) {
            WifiP2pDevice device = (WifiP2pDevice) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.d(WiFiDirect.TAG, "Device status -" + device.status);

            String myMac = device.deviceAddress;
            mWiFiDirectUtils.mThisDeviceMACP2p = myMac;
            Log.d(WiFiDirect.TAG, "This Device WiFi P2p MAC Address: " + myMac);
            
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
            	WifiP2pDeviceList mList = (WifiP2pDeviceList) intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
            	if (mList == null)
            		return;
            	for (WifiP2pDevice mDevice : mList.getDeviceList()) {
            		Log.i(WiFiDirect.TAG, "Peer Available: " + mDevice.deviceName);
            	}
                //manager.requestPeers(channel, mWiFiDirectUtils);
            }
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
        	if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
            	Log.i(WiFiDirect.TAG, "WiFi P2P Discover process started.");
            } else {
            	Log.i(WiFiDirect.TAG, "WiFi P2P Discover process stopped.");
            }
        }
    }
}
