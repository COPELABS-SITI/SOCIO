package com.copelabs.oiframework.contentmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

public class FileIO {
	
	private final static String TAG = "FileIO";
	
	
	public final static String TOSEND = "toSend";
	public final static String LOCALCACHE = "localCache";
	private final static String mOthersFolder = "/Oi2.0/ToSend/";
	private final static String mOthersFile = "toSend.xml";
	
	private final static String mNodeFolder = "/Oi2.0/LocalCache/";
	private final static String mNodeFile = "localCache.xml";
	
	
	/**
	 * Writes the messages to be sent to an xml file in internal storage (private)
	 * The caller choose which file it want to write.
	 * @param mFileType
	 * @param mPacket
	 */
	  
	public static boolean writeFile(String mFileType, Packet mPacket) {

		String mFolder;
		String mFile;
		if (mFileType.equals(TOSEND)) {
			mFolder = mOthersFolder;
			mFile = mOthersFile;
		} else if (mFileType.equals(LOCALCACHE)) {
			mFolder = mNodeFolder;
			mFile = mNodeFile;
		} else {
			return false;
		}
		
		String recordPacket="";

		byte[] contentInBytes;
		
		File folder=new File(Environment.getExternalStorageDirectory().toString() + mFolder);
		folder.mkdirs();
		String dirPath=folder.toString();
		File file = new File(dirPath, mFile);
                
        try  {
        	// if file doesn't exists, then create it
        	FileOutputStream fop;
    		if (!file.exists()) {
    			// opens the output stream to file, to append content
            	fop = new FileOutputStream(file);
	    		// creates the xml header in the file
            	recordPacket="<?xml version='1.0' encoding='UTF-8'?>\n";
	    		contentInBytes = recordPacket.getBytes();
	    		fop.write(contentInBytes);
	    		Log.v(TAG,"file created");
    		} else {
    			fop = new FileOutputStream(file,true);
    		}
            //TODO: message MUST be stored encrypted
    		recordPacket=mPacket.getXmlEntry();
            contentInBytes=recordPacket.getBytes();
        	fop.write(contentInBytes);
            fop.flush();
            fop.close();
            return true;
              
    	} catch (IOException e) {
    		Log.e(TAG, "Error when writting to the file " + mFileType);
    		return false;
    	}    
	}
	
	public static boolean writeListFile(String mFileType, List<Packet> mList, boolean mAppend) {

		String mFolder;
		String mFile;
		if (mFileType.equals(TOSEND)) {
			mFolder = mOthersFolder;
			mFile = mOthersFile;
		} else if (mFileType.equals(LOCALCACHE)) {
			mFolder = mNodeFolder;
			mFile = mNodeFile;
		} else {
			return false;
		}
		
		String recordPacket="";

		byte[] contentInBytes;
		
		File folder=new File(Environment.getExternalStorageDirectory().toString() + mFolder);
		folder.mkdirs();
		String dirPath=folder.toString();
		File file = new File(dirPath, mFile);
                
        try  {
        	// if file doesn't exists, then create it
        	FileOutputStream fop;
    		if (!file.exists()) {
    			// opens the output stream to file, to append content
            	fop = new FileOutputStream(file);
	    		// creates the xml header in the file
            	recordPacket="<?xml version='1.0' encoding='UTF-8'?>\n";
	    		contentInBytes = recordPacket.getBytes();
	    		fop.write(contentInBytes);
	    		Log.v(TAG,"file created");
    		} else {
    			fop = new FileOutputStream(file, mAppend);
    		}
            //TODO: message MUST be stored encrypted
    		for (Packet mPacket : mList) {
    			recordPacket=mPacket.getXmlEntry();
    			contentInBytes=recordPacket.getBytes();
    			fop.write(contentInBytes);
    		}
            fop.flush();
            fop.close();
            return true;
              
    	} catch (IOException e) {
    		Log.e(TAG, "Error when writting to the file " + mFileType);
    		return false;
    	}    
	}
	
	/**
	 * Read the messages at the file chosen by the caller.
	 * @param mFileType
	 */
	public static List<Packet> readFile(String mFileType) {
		
		String mFolder;
		String mFile;
		if (mFileType.equals(TOSEND)) {
			mFolder = mOthersFolder;
			mFile = mOthersFile;
		} else if (mFileType.equals(LOCALCACHE)) {
			mFolder = mNodeFolder;
			mFile = mNodeFile;
		} else {
			return null;
		}
		
		File folder=new File(Environment.getExternalStorageDirectory().toString() + mFolder);
		if (!folder.exists())
			return null;
		
		String dirPath=folder.toString();
		File file = new File(dirPath, mFile);
		if (!file.exists())
			return null;
		
		XmlPullParserHandler Messages = new XmlPullParserHandler();
		List<Packet> mPackets= new ArrayList<Packet>();
		
		try {
		    FileInputStream fin = new FileInputStream(file);
		    mPackets = Messages.parse(fin);
		    return mPackets;
		  
		}catch(IOException e) {
	        return null;
	    } 
	}
}
