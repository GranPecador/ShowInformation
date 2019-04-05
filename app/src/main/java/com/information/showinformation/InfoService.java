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
                        getInfoFromDB(address);
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

        //Log.i(TAG, "Connect to MariaDB");
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:mariadb://192.168.1.109/information_db";
        String user = "root";
        String pass = "dayoneday";
        try {
            mConnection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
            intent.putExtra(MainActivity.PARAM_MESSAGE, MainActivity.PARAM_NOT_CONNECT_TO_DB);
            sendBroadcast(intent);
        }
    }

    private void getInfoFromDB(String address) {
        try {
            PreparedStatement preparedStatement = mConnection.prepareStatement(
                    "SELECT text_info FROM\n" +
                            "information  INNER JOIN information_topics \n" +
                            "ON information.information_id = information_topics.information_id\n" +
                            "INNER JOIN persons_topics\n" +
                            "ON  information_topics.topic_id = persons_topics.topic_id\n" +
                            "WHERE  persons_topics.person_id IN\n" +
                            "(SELECT DISTINCT persons_devices.person_id \n" +
                            "from devices  \n" +
                            "INNER JOIN persons_devices \n" +
                            "ON persons_devices.device_id = devices.device_id\n" +
                            "WHERE devices.address = ?)\n" +
                            "AND\n" +
                            "(information.time_frame_end >= NOW() \n" +
                            "OR inform  ation.time_frame_end IS NULL);");
            preparedStatement.setNString(1, address);
            ResultSet rs = preparedStatement.executeQuery();

            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
            intent.putExtra(MainActivity.PARAM_MESSAGE, MainActivity.PARAM_TIME);
            sendBroadcast(intent);
            while (rs.next()) {

                final String info = rs.getString(1);
                Intent mesIntent = new Intent(MainActivity.BROADCAST_ACTION);
                mesIntent.putExtra(MainActivity.PARAM_MESSAGE, MainActivity.GET_INFO);
                mesIntent.putExtra(MainActivity.PARAM_INFO, info);
                sendBroadcast(mesIntent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
