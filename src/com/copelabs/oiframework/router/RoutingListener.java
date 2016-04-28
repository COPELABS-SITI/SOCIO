package com.copelabs.oiframework.router;

import java.util.List;
import java.util.Map;

import com.copelabs.oiaidllibrary.UserDevice;
import com.copelabs.oiframework.contentmanager.Packet;

public interface RoutingListener {
	void onPacketReceived(List<Packet> mListOfPackets);
	Map<String, List<Packet>> getListOfPackets(List<UserDevice> mListOfUserDevices);
	void newDeviceFound(UserDevice mDevice);
	void deviceLost(UserDevice mDevice);
	void contactListUpdated(List<UserDevice> mListOfPackets);
	void error(int mError);
}