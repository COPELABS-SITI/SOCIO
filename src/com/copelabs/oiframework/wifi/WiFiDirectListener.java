package com.copelabs.oiframework.wifi;

import java.util.List;
import java.util.Map;

import com.copelabs.oiframework.contentmanager.Packet;

public interface WiFiDirectListener {
	void onPacketReceived(List<Packet> mListOfPackets);
	void onNewWiFiDirectDeviceFound(WiFiDirectDevice mDevice, Map<String, String> mListOfSocialWeight);
	void onWiFiDirectDeviceDisappear(WiFiDirectDevice mDevice);
	void error(int mError);
}