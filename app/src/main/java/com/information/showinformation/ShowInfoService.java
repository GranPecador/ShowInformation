package com.information.showinformation;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.information.showinformation.adapters.InformationAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShowInfoService extends Service {

    private IBinder mBinder;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private Future longRunningTaskFuture;
    protected RecyclerView mRecyclerView;
    private InformationAdapter mInfoAdapter;
    Context context = this;
    private Integer currentNumber = 0;

    ReceiverUdpRunnable mUdpSocket;

    BroadcastReceiver mReceiveMessagesFromServer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("receive_message".equals(action)){
                String message = intent.getStringExtra("message");
                parseMessage(message);
            }
        }
    };

    private void parseMessage(String message) {
        Log.e("message", message);
        String[] mesDer = message.split("###");
        Integer num = Integer.parseInt(mesDer[0]);
        if(num>currentNumber) {
            currentNumber = num;

            mInfoAdapter.clearAll();
            mInfoAdapter.notifyDataSetChanged();
            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
            intent.putExtra("message", "clear");
            sendBroadcast(intent);
        }
            mInfoAdapter.addItem((mesDer[1]));
            mInfoAdapter.notifyDataSetChanged();
            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
            intent.putExtra("message", mesDer[1]);
            sendBroadcast(intent);

    }

    public ShowInfoService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "onCreate()");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiveMessagesFromServer, new IntentFilter("receive_message"));

        mUdpSocket = new ReceiverUdpRunnable(this);
        longRunningTaskFuture = mExecutorService.submit(mUdpSocket);


        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent.putExtra("message", "receive Message from service");
        //sendBroadcast(intent);
    }

    public void setAdapter(InformationAdapter adapter) {
        this.mInfoAdapter = adapter;
        //mRecyclerView.setAdapter(mInfoAdapter);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiveMessagesFromServer);
        Log.e("service", "onDestroy");
        mUdpSocket.setEndReceive();
        longRunningTaskFuture.cancel(true);
    }
}
