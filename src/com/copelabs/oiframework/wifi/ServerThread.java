
package com.copelabs.oiframework.wifi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.copelabs.oiframework.contentmanager.Packet;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public class ServerThread extends Thread {

    private final int THREAD_COUNT = 10;
    public static final int SERVER_PORT = 4545;
    private Handler handler;
    private static final String TAG = "GroupOwnerSocketHandler";
    private Map<String, List<Packet>> mToSend;

    public ServerThread(Handler handler, Map<String, List<Packet>> mToSend) throws IOException {
        this.handler = handler;
        this.mToSend = mToSend;
    }

   
    @Override
    public void run() {
        Socket client = null;
        ServerSocket serverSocket = null;
        try {
        	serverSocket = new ServerSocket(SERVER_PORT);
        	serverSocket.setSoTimeout(20000);

        	Log.i(TAG, "Waiting for the client");
            client = serverSocket.accept();
            Log.i(TAG, "Client connected");
            
            DataInputStream dis = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            
            // RECEIVE DATA
            int len = dis.readInt();
            byte[] data = new byte[len];
            if (len > 0) {
                dis.readFully(data);
            
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bis);
                List<Packet> mData = (List<Packet>) ois.readObject();
                
                if (mData == null) {
                	Message msg = new Message();
                	Bundle mPackets = new Bundle();
                	msg.what = WiFiDirectUtils.EMPTY;
                	msg.setData(mPackets);
                	handler.sendMessage(msg); 
                	return;
                }
                
            	Log.i(TAG, "List Received: " + mData.toString());
            	for (Packet info : mData) {
            		Log.i(TAG, "Destination: " + info.getIdDestination());
            		Log.i(TAG, "Destination Name: " + info.getNameDestination());
            		Log.i(TAG, "Source: " + info.getIdSource());
            		Log.i(TAG, "Source Name: " + info.getNameSource());
            		Log.i(TAG, "Application: " + info.getApplication());
            		Log.i(TAG, "Message: " + info.getMessage());
            	}
            	Message msg = new Message();
            	Bundle mPackets = new Bundle();
            	mPackets.putSerializable("data", (Serializable) mData);
            	msg.what = WiFiDirectUtils.MESSAGE_READ;
            	msg.setData(mPackets);
            	handler.sendMessage(msg);      
            }
            
            // SEND DATA
        	String mClientMACAddress = dis.readUTF();
        	Log.i(TAG, "Client MAC: " + mClientMACAddress);
        	
        	
        	if (!mToSend.containsKey(mClientMACAddress)) {
        		Log.i(TAG, "Nothing to reply");
        		dos.writeInt(0);
        		dos.flush();
        	} else {
        		Log.i(TAG, "Replying with list");
        		List<Packet> mListToSend = mToSend.get(mClientMACAddress);
        		
        		ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bao);
                oos.writeObject(mListToSend);
                oos.close();
                
                byte[] byteToTransfer = bao.toByteArray();
                
                dos.writeInt(byteToTransfer.length);
                dos.flush();
                dos.write(byteToTransfer, 0, byteToTransfer.length);
                dos.flush();
        	}
        	dis.close();
        	dos.close();
        	Log.i(TAG, "Server all work done");
        	client.close();
        	serverSocket.close();
        } catch (IOException | RejectedExecutionException | ClassNotFoundException e) {
        	e.printStackTrace();
            try {
            	if (client != null)
            		client.close();
                if (serverSocket != null && !serverSocket.isClosed()) {
                	serverSocket.close();
                    Log.i(TAG, "Server Socket closed");
                }
            } catch (IOException ioe) {
            	e.printStackTrace();
            }
        }
        
    }

}
