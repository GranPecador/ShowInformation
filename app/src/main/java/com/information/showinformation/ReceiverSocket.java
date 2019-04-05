package com.information.showinformation;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiverSocket extends Thread {

    private boolean running = false;
    private DatagramSocket socket;

    public ReceiverSocket() {

    }

    @Override
    public void run() {
        super.run();
        runServer();
    }

    private void runServer(){
        running = true;

        try {
            socket = new DatagramSocket(6344);

            while (running) {

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
