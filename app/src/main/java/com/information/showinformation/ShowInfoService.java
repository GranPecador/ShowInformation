package com.information.showinformation;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.information.showinformation.adapters.InformationAdapter;
import com.information.showinformation.models.InformationModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShowInfoService extends Service {

    private IBinder mBinder;

    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
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
        String[] mesDer = message.split("#");
        Integer num = Integer.parseInt(mesDer[0]);
        if(num>currentNumber) {
            currentNumber = num;
            //mInfoAdapter.clear();
            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
            intent.putExtra("message", "clear");
            sendBroadcast(intent);
        }
        mInfoAdapter.addItem(new InformationModel(mesDer[1]));
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

        mInfoAdapter = new InformationAdapter();

        mUdpSocket = new ReceiverUdpRunnable(this);
        longRunningTaskFuture = mExecutorService.submit(mUdpSocket);

        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent.putExtra("message", "receive Message from service");
        //sendBroadcast(intent);
    }

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        mRecyclerView.setAdapter(mInfoAdapter);
        //new ShowMessages(this.mRecyclerView).execute();
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

    protected static class ShowMessages extends AsyncTask<Void, String, Void> {

        private InformationAdapter mInfoAdapter;
        private WeakReference<RecyclerView> mRecyclerReference;


        ShowMessages(RecyclerView recyclerView) {
            mRecyclerReference = new WeakReference<RecyclerView>(recyclerView);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInfoAdapter = new InformationAdapter();
            mRecyclerReference.get().setAdapter(mInfoAdapter);
            mInfoAdapter.addItem(new InformationModel("onPreExecution"));

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mInfoAdapter.addItem(new InformationModel("onPostExecution"));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("Service","service doBackground()");
            publishProgress("onProgressUpdate");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mInfoAdapter.addItem(new InformationModel(values[0]));
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
