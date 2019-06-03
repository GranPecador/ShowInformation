package com.information.showinformation;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.information.showinformation.adapters.InformationAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public final static String BROADCAST_ACTION = "com.information.showinformation";
    public final static String PARAM_MESSAGE = "message";
    public final static String PARAM_INFO = "information";
    public final static String GET_INFO = "get_information";
    public final static String PARAM_NOT_CONNECT_TO_DB = "can_not_connect_to_db";
    public final static String PARAM_NOT_CONNECT_TO_INTERNET = "can_not_connect_to_internet";
    public final static String PARAM_TIME = "timer";


    private RecyclerView mInfoRecyclerList;
    private RecyclerView.Adapter mInfoAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private ShowInfoService mShowInfoService;
    private boolean mBound;
    public ArrayAdapter<String> mAdapter;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messag = intent.getStringExtra("message");
            System.out.print(messag+"////////////////////////////////////////////Main");
            if (messag.equals("clear")){
                mAdapter.clear();
            }else {
                mAdapter.add(messag);
            }    String message = intent.getStringExtra(PARAM_MESSAGE);

            switch (message){
                case GET_INFO:
                    String info = intent.getStringExtra(PARAM_INFO);
                    if(mAdapter.getPosition(info) == -1) {
                        mAdapter.add(info);
                    }
                    break;
                case PARAM_TIME:
                    if (System.currentTimeMillis()-lastTime > 20000){
                        mAdapter.clear();
                    }
                    break;
            }

        }
    };
    private ListView mInfoList;
    private long lastTime = System.currentTimeMillis();

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ShowInfoService.ShowInfoBinder serviceBinder = (ShowInfoService.ShowInfoBinder) service;
            mShowInfoService = serviceBinder.getShowInfoService();
            mShowInfoService.setRecyclerView(mInfoRecyclerList);
            mBound = true;
            Log.e("Main", "onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ShowInfoService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.e("Main", "onStart()");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoList = findViewById(R.id.info_view);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mInfoList.setAdapter(mAdapter);


        IntentFilter mesIntentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(mBroadcastReceiver, mesIntentFilter);

        mInfoRecyclerList = findViewById(R.id.info_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mInfoRecyclerList.setLayoutManager(mLayoutManager);
        mInfoRecyclerList.setAdapter(null);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
            Log.e("Main", "onStop()");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
