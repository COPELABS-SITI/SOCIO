/**
 * @file Packet.java
 * COPELABS - Oi!
 * @author Rute Sofia (COPELABS/ULHT)
 * @date 20/05/2015
 * @brief Item  corresponds to the object messages sent by Oi
 *  * @version v1.0 - pre-prototype
 */
package com.copelabs.oiframework.contentmanager;

import java.io.Serializable;

/**
 * @param idSource the identifier of the owner's contact (BT MAC)
 * @param nameSource the name of the Sender contact (BT Name)
 * @param idDestination the identifier of the selected contact (BT MAC)
 * @param nameDestination the name of the Receiver contact (BT Name)
 * @param message the message content
 * @param timestamp the timestamp for writing the message
 */
public class Packet implements Serializable{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TAG_ID_SOURCE = "idSource";
	 public static final String TAG_NAME_SOURCE = "nameSource";
	 public static final String TAG_ID_DESTINATION = "idDestination";
	 public static final String TAG_NAME_DESTINATION = "nameDestination";
	 public static final String TAG_APPLICATION = "application";
	 public static final String TAG_MESSAGE = "message";
	 public static final String TAG_TIMESTAMP = "timestamp";
	
	 private String idSource;
	 private String nameSource;
	 
	 private String idDestination;
	 private String nameDestination;
	 
	 private String application;

     //message content
     private String message="";
     
     //timestamp for sending the message
     private long timestamp;
     
     public Packet(){
    	 super();
     }
     public Packet(String myBTMAC, String myBTName) {
    	 this.idSource = myBTMAC;
    	 this.nameSource = myBTName;
     }
     
     
     public String getIdSource() {  
    	 return idSource;  
     } 
     
     public String getIdDestination() {  
         return idDestination;  
     }
  
      
     public String getNameSource() {  
    	 return nameSource;  
     }  
     
     public String getNameDestination() {  
    	 return nameDestination;  
     }
     
     public String getApplication() {
    	 return application;
     }
     
     public String getMessage()  {
    	 return message;	
     }
     
     public long getTimestamp()  {
    	 return timestamp;	
     }
    
     public void setIdSource(String idSource) {
    	 this.idSource = idSource;
     }
     
     public void setNameSource(String nameSource) {  
    	 this.nameSource = nameSource;  
     }
     
     public void setIdDestination(String idDestination) {
    	 this.idDestination = idDestination;
     }
    
     public void setNameDestination(String nameDestination) {  
    	 this.nameDestination = nameDestination;  
     }
     
     public void setApplication(String application) {
    	 this.application = application;
     }
    
     public void setAttributes(String idSource, String nameSource ,String idDestination, String nameDestination, String application, String message, long timestamp) {
    	 this.idSource = idSource;
    	 this.nameSource = nameSource;
    	 this.idDestination = idDestination;
    	 this.nameDestination = nameDestination;
    	 this.application = application;
    	 this.message = message;
    	 this.timestamp = timestamp;
     }
     
     public void setMessage(String msg) {  
         this.message = msg;  
     } 
     
     public void setTimestamp(long ts) {  
    	 this.timestamp = ts;  
     }
     
     /**
      * Writes an OiMessage (List) in xml format
      * @return String an entry containing the xml format for an OiMessage
      */
     public String getXmlEntry() {

    	 String format =
	            "<oimessage>\n" +
	            "         <" + TAG_ID_SOURCE + ">%s</" + TAG_ID_SOURCE +">\n" +
	            "         <" + TAG_NAME_SOURCE + ">%s</" + TAG_NAME_SOURCE + ">\n" +
	            "         <" + TAG_ID_DESTINATION + ">%s</" + TAG_ID_DESTINATION + ">\n" + 
	            "         <" + TAG_NAME_DESTINATION + ">%s</" + TAG_NAME_DESTINATION + ">\n" +
	            "         <" + TAG_APPLICATION + ">%s</" + TAG_APPLICATION + ">\n" +
	            "         <" + TAG_MESSAGE + ">%s</" + TAG_MESSAGE + ">\n" +
	            "         <" + TAG_TIMESTAMP + ">%d</" + TAG_TIMESTAMP + ">\n" + 
	            "</oimessage>\n";
		
		return String.format(format,this.idSource, this.nameSource, this.idDestination, this.nameDestination, this.application, this.message, this.timestamp);
	 }
     
}  
