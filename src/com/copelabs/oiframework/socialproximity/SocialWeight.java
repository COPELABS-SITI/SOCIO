package com.copelabs.oiframework.socialproximity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @version 1.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the SOCIO application.
 * This class holds information about a device: name, MAC address, and social weight towards it.
 * @author Waldir Moreira (COPELABS/ULHT)
 */
public class SocialWeight implements Parcelable {
	private String mMacAddress;
	private String mDeviceName;
	private double mSocialWeight;
	
	public static final String socialWeight_key = "socialReceiver";
	
	/**
	* This method is the constructor for SocialWeight.
	* @param mMacAddress The MAC address of the device.
	* @param mDeviceName The name of the device.	
	* @param mSocialWeight The social weight towards the device.
	**/
	public SocialWeight(String mMacAddress, String mDeviceName, double mSocialWeight) {
		this.mMacAddress = mMacAddress;
		this.mDeviceName = mDeviceName;
		this.mSocialWeight = mSocialWeight;
	}
	
	/**
	* This method is the constructor for SocialWeight.
	* @param source The Parcel source from which information can be extracted.
	**/
	private SocialWeight (Parcel source){
		this.mMacAddress = source.readString();
		this.mDeviceName = source.readString();
		this.mSocialWeight = source.readLong();
	}  
	
	/**
	* This method sets the device's MAC address.
	* @param mMacAddress The device's MAC address.
	**/
	public void setMacAddress(String mMacAddress){
		this.mMacAddress = mMacAddress;
	}
	
	/**
	* This method gets the device's MAC address.
	**/
	public String getMacAddress(){
		return mMacAddress;
	}
	
	/**
	* This method sets the device's name.
	* @param mDeviceName The device's name.
	**/
	public void setDeviceName(String mDeviceName){
		this.mDeviceName = mDeviceName;
	}
	
	/**
	* This method gets the device's name.
	**/
	public String getDeviceName (){
		return mDeviceName;
	}
	
	/**
	* This method sets the device's social weight.
	* @param mSocialWeight The device's social weight.
	**/
	public void setSocialWeight(long mSocialWeight){
		this.mSocialWeight = mSocialWeight;
	}
	
	/**
	* This method gets the device's social weight.
	**/
	public double getSocialWeight (){
		return mSocialWeight;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		 dest.writeString(mMacAddress);
	     dest.writeString(mDeviceName);		
	     dest.writeDouble(mSocialWeight);	
	}
	
	public final Creator CREATOR = new Creator() {
		@Override
	    public SocialWeight createFromParcel(Parcel in) {
	        return new SocialWeight(in);
	    }
		@Override
	    public SocialWeight[] newArray(int size) {
	        return new SocialWeight[size];
	    }
	};
	
}
