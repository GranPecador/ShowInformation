package com.information.showinformation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ShowInfoService extends Service {

    private IBinder mBinder;
    private Net receiverFromServer = new Net();
    private Thread receiverRun;

    private ShowInfoService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        receiverRun = new Thread(new Runnable() {
            @Override
            public void run() {
                receiverFromServer.openReceiverConnection();
                receiverFromServer.receive();
            }
        },"receiverRun");
        receiverRun.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null)
            mBinder = new ShowInfoBinder();
        return mBinder;
    }

    public class ShowInfoBinder extends Binder {
        ShowInfoService getShowInfoService(){
            return ShowInfoService.this;
        }
    }

    private static String list;
    public void sendToService(String message){
        list = message;

        Intent messageIntent = new Intent(MainActivity.BROADCAST_ACTION);
        messageIntent.putExtra("send message", "get message");
        messageIntent.putExtra("message", list);
        sendBroadcast(messageIntent);
    }
}
