package com.information.showinformation;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoService extends Service {

    private final IBinder mBinder = new ServiceBinder();
    private Connection mConnection;
    private ServerSocket mServerSocket;

    private final static int SERVER_PORT = 6000;

    public InfoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mServerSocket = new ServerSocket(SERVER_PORT);
                    while (isOnline()) {

                        // Подключение к порту. По сути, начало работы сервера.
                        Socket server = mServerSocket.accept();
                        // Получение данных от клиента.
                        BufferedReader fromClient = new BufferedReader(new InputStreamReader(server.getInputStream()));
                        String address = fromClient.readLine();

                        if (mConnection == null) {
                            connectToMariaDB();
                        }
                    }
                    if(!mServerSocket.isClosed()){
                        mServerSocket.close();}
                    if (!isOnline()) {
                        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                        intent.putExtra(MainActivity.PARAM_MESSAGE, MainActivity.PARAM_NOT_CONNECT_TO_INTERNET);
                        sendBroadcast(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
//        if (mBinder == null){
//            mBinder = new MyBinder();
//        }
//        return mBinder;
    }

    public class ServiceBinder extends Binder {
        InfoService getInfoService() {
            return InfoService.this;
        }
    }

    private boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void connectToMariaDB() {

            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
            intent.putExtra(MainActivity.PARAM_MESSAGE, MainActivity.PARAM_NOT_CONNECT_TO_DB);
            sendBroadcast(intent);
    }

}
