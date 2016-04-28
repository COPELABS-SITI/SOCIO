package com.copelabs.oiframework.contentmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import android.util.Log;

public class CleanTask extends TimerTask {

    private String TAG = "TTL_Check";
    private int timeout = 60000 * 60;  //1 hour

    // Write Custom Constructor to pass Context
    public CleanTask() {
    }

	@Override
	public void run() {
		Log.i(TAG, "Checking Packets TTL...");
		// Check TTL from packets located at TOSEND file
		List<Packet> mListToSendPackets = FileIO.readFile(FileIO.TOSEND);
		if (mListToSendPackets != null) {
			if (!mListToSendPackets.isEmpty()) {
				mListToSendPackets = checkTTL(mListToSendPackets);
				if (mListToSendPackets !=null)
					FileIO.writeListFile(FileIO.TOSEND, mListToSendPackets, false);
			}
		}
		
		// Check TTL from packets located at LOCALCACHE file
		List<Packet> mListLocalCachePackets = FileIO.readFile(FileIO.LOCALCACHE);
		if (mListLocalCachePackets != null) {
			if (mListLocalCachePackets.isEmpty()) {
				mListLocalCachePackets = checkTTL(mListToSendPackets);
				if (mListLocalCachePackets !=null)
					FileIO.writeListFile(FileIO.LOCALCACHE, mListLocalCachePackets, false);
			}
		}
		Log.i(TAG, "Checking Packets TTL - DONE");
	}
	
	private List<Packet> checkTTL (List<Packet> mList) {
		if (mList == null)
			return null;
		List<Packet> mNewListAllPackets = new ArrayList<Packet>();
		long mTimeNow = System.currentTimeMillis();
		for (Packet mPacket : mList) {
			long diff = mTimeNow - mPacket.getTimestamp();
			if (diff < timeout) {
				mNewListAllPackets.add(mPacket);
				Log.i(TAG, "Packet to " + mPacket.getNameDestination() + " still inside TTL");
			} else {
				Log.i(TAG, "Packet to " + mPacket.getNameDestination() + " removed");
			}
			
		}
		return mNewListAllPackets;
	}
}