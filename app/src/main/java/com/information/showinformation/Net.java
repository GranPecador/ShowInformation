package com.information.showinformation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Net {

    private Thread receiver;
    private DatagramSocket mDatagramSocket = null;
    private int mPort = 6334;
    private String message;


    public void openReceiverConnection() {
        try {
            mDatagramSocket = new DatagramSocket(mPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public String receive(){
        receiver = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        mDatagramSocket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    message = new String(packet.getData());
                }
            }
        });
        receiver.start();
        return message;
    }
}
