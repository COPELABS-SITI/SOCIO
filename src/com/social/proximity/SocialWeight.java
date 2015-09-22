/**
 * SocialWeight is a parcelable class that allows to get Bluetooth device
 * information (MAC address, name, and social weight) upon changes 
 * in the datadabe.
 *
 * @author Waldir Moreira (ULHT)
 * waldir.junior@ulusofona.pt
 *
 * @version 0.1
 *
 */

package com.social.proximity;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialWeight implements Parcelable {
	public static final String socialWeight_key = "socialReceiver";
	private String mMacAddress;
	private String mDeviceName;
	private int mSocialWeight;
	
	/**
	 * Sets the MAC address.
	 * @param mMacAddress The MAC address of the device.
	 */
	public void setMacAddress(String mMacAddress){
		this.mMacAddress = mMacAddress;
	}
	
	/**
	 * Gets the MAC address.
	 * @return mMacAddress The MAC address of the device.
	 */
	public String getMacAddress(){
		return mMacAddress;
	}
	
	/**
	 * Sets the device's name.
	 * @param mDeviceName The name of the device.
	 */
	public void setDeviceName(String mDeviceName){
		this.mDeviceName = mDeviceName;
	}
	
	/**
	 * Gets the device's name.
	 * @return mDeviceName The name of the device.
	 */
	public String getDeviceName (){
		return mDeviceName;
	}
	
	/**
	 * Sets the social weight towards the device.
	 * @param mSocialWeight The social weight towards the device.
	 */
	public void setSocialWeight(int mSocialWeight){
		this.mSocialWeight = mSocialWeight;
	}
	
	/**
	 * Gets the social weight towards the device.
	 * @return mSocialWeight The social weight towards the device.
	 */
	public int getSocialWeight (){
		return mSocialWeight;
	}
	
	/**
	 * SocialWeight constructor.
	 */
	public SocialWeight () {
	}
	
	/**
	 * SocialWeight constructor.
	 * @param source The object containing the MAC address, name and social weight towards a device.
	 */
	public SocialWeight (Parcel source){
		this.mMacAddress = source.readString();
		this.mDeviceName = source.readString();
		this.mSocialWeight = source.readInt();
	}  
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		 dest.writeString(mMacAddress);
	     dest.writeString(mDeviceName);		
	     dest.writeInt(mSocialWeight);	
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
