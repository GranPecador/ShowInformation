package com.information.showinformation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TimeFormatException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static com.information.showinformation.MainActivity.BROADCAST_ACTION;

public class ReceiverUdpRunnable implements Runnable {

    private boolean isReceive = true;
    private int port = 13052;
    private DatagramSocket mReceiveSocket;
    private DatagramPacket mReceivePacket;
    private byte[] buff = new byte[2000];
    private Context context;

    public ReceiverUdpRunnable(Context context){
        this.context = context;
    }

    @Override
    public void run() {
        Log.e("Run", "next step while");
        closeUdpReceiveData();
        try {

            mReceiveSocket = new DatagramSocket(port);
            Log.e("DatagramSocket", "new DatagramSocket port: "+ mReceiveSocket.getPort()+ " exect: "+ mReceiveSocket.getLocalPort());

        } catch (SocketException e) {
            Log.e("DatagramSocket",e.getLocalizedMessage());
            e.printStackTrace();
        }
        mReceivePacket = new DatagramPacket(buff, buff.length);
        Log.e("mReceivePacket", "new DatagramPacket");
        while(isReceive) {
            Log.e("Run", "isReceive = true");
            try {
                mReceiveSocket.receive(mReceivePacket);
                Log.e("DatagramSocket", "receive");

                String result = new String(mReceivePacket.getData(), mReceivePacket.getOffset(), mReceivePacket.getLength());
                Log.e("Udp", "dfjgkdfkgjkfdjgkfjd");
                Intent sendMessage = new Intent("receive_message");
                sendMessage.putExtra("message", result);
                LocalBroadcastManager.getInstance(context).sendBroadcast(sendMessage);
            } catch (IOException e) {
                Log.e("run Socket error", e.getMessage());
                continue;
            }
        }
    }

    public void closeUdpReceiveData() {
        Log.e("Run", "try close socket");
        if (mReceiveSocket != null) {
            if (mReceiveSocket.isConnected())
                mReceiveSocket.disconnect();
            if (!mReceiveSocket.isClosed())
                mReceiveSocket.close();
            Log.e("Run", "socket != null. isBound = "+mReceiveSocket.isBound()+
                    " isConnected = "+mReceiveSocket.isConnected()+" isClosed = "+ mReceiveSocket.isClosed());

            mReceiveSocket = null;
        }
    }

    public void setEndReceive() {

        isReceive = false;
        closeUdpReceiveData();
        Log.e("udp", "isReceive =" + isReceive);
    }

}
