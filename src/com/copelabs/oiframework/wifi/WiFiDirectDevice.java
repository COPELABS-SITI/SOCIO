package com.copelabs.oiframework.wifi;

import android.net.wifi.p2p.WifiP2pDevice;
/**
 * A structure to hold service information.
 */
public class WiFiDirectDevice {
    private WifiP2pDevice device;
    private String instanceName = null;
    private String serviceRegistrationType = null;
    private long TTL;
    
    WiFiDirectDevice(WifiP2pDevice device, String instanceName, String serviceRegistrationType, long TTL) {
    	this.device = device;
    	this.instanceName = instanceName;
    	this.serviceRegistrationType = serviceRegistrationType;
    	this.TTL = TTL;
    }
    
    public WiFiDirectDevice() {
    	super();
	}

	public void setDevice(WifiP2pDevice device) {
    	this.device = device;
    }
    
    public void setInstanceName(String instanceName) {
    	this.instanceName = instanceName;
    }
    
    public void setServiceRegistrationType(String serviceRegistrationType) {
    	this.serviceRegistrationType = serviceRegistrationType;
    }
    
    public void setLastTimeSeen(long mTime) {
    	this.TTL = mTime;
    }
    
    public WifiP2pDevice getDevice() {
    	return device;
    }
    
    public String getInstanceName() {
    	return instanceName;
    }
    
    public String getServiceRegistrationType() {
    	return serviceRegistrationType;
    }
    
    public long getLastTimeSeen() {
    	return TTL;
    }
}
