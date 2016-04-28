
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import com.copelabs.oiframework.contentmanager.Packet;

public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";
    private Handler handler;
    private InetAddress mAddress;
    private List<Packet> mDataToSend;
    private String mThisDeviceMACP2p;
    
    public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress, List<Packet> list, String mThisDeviceMACP2p) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
        this.mDataToSend = list;
        this.mThisDeviceMACP2p = mThisDeviceMACP2p;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
            		ServerThread.SERVER_PORT), 10000);
            Log.i(TAG, "Client socket - " + socket.isConnected());
            
            
            // SEND LIST
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(mDataToSend);
            oos.close();
            
            byte[] byteToTransfer = bao.toByteArray();
            
            /*OutputStream out = socket.getOutputStream(); 
            DataOutputStream dos = new DataOutputStream(out);
            InputStream in = socket.getInputStream(); 
            DataInputStream  = new DataInputStream(in);*/
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            
            dos.writeInt(byteToTransfer.length);
            dos.flush();
            dos.write(byteToTransfer, 0, byteToTransfer.length);
            dos.flush();
            dos.writeUTF(mThisDeviceMACP2p);
            dos.flush();
            

            // RECEIVE LIST
            
            
            int len = dis.readInt();
            byte[] data = new byte[len];
            if (len > 0) {
                dis.readFully(data);
            
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
	            ObjectInputStream ois = new ObjectInputStream(bis);
	            List<Packet> persons = (List<Packet>) ois.readObject();
	            
	        	Log.i(TAG, "List Received: " + persons.toString());
	        	for (Packet info : persons) {
	        		Log.i(TAG, "Destination: " + info.getIdDestination());
	        		Log.i(TAG, "Destination Name: " + info.getNameDestination());
	        		Log.i(TAG, "Source: " + info.getIdSource());
	        		Log.i(TAG, "Source Name: " + info.getNameSource());
	        		Log.i(TAG, "Application: " + info.getApplication());
	        		Log.i(TAG, "Message: " + info.getMessage());
	        	}
	        	Message msg = new Message();
	        	Bundle mPackets = new Bundle();
	        	mPackets.putSerializable("data", (Serializable) persons);
	        	msg.what = WiFiDirectUtils.MESSAGE_READ;
	        	msg.setData(mPackets);
	        	handler.sendMessage(msg);
            } else {
            	Log.i(TAG, "Nothing to receive");
            }
        	dis.close();
        	dos.close();
        	Log.i(TAG, "Socket closed");
        	socket.close();

        	Log.i(TAG, "Done");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
            	Log.e(TAG, "Socket closed");
            	Message msg = new Message();
                socket.close();
                msg.what = WiFiDirectUtils.SOCKETERROR;
                handler.sendMessage(msg);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

}
