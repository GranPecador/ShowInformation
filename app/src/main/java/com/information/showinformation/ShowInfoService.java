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
    Context context = this;

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

        System.out.print(message+"////////////////////////////////////////////");


    }

    public ShowInfoService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "onCreate()");
        registerReceiver(mReceiveMessagesFromServer, new IntentFilter("receive_message"));

        mUdpSocket = new ReceiverUdpRunnable();
        longRunningTaskFuture = mExecutorService.submit(mUdpSocket);

        Context context = getApplicationContext();

        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent.putExtra("message", "receive Message from service");
        sendBroadcast(intent);
    }

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        new ShowMessages(this.mRecyclerView).execute();
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

    protected static class ShowMessages extends AsyncTask<Void, Void, Void> {

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

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            mInfoAdapter.addItem(new InformationModel("onProgressUpdate"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiveMessagesFromServer);
        Log.e("service", "onDestroy");
        mUdpSocket.setEndReceive();
        longRunningTaskFuture.cancel(true);


    }
}
