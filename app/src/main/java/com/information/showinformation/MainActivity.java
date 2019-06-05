package com.information.showinformation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.information.showinformation.models.InformationModel;

public class MainActivity extends AppCompatActivity
                            implements ItemFragment.OnListFragmentInteractionListener{

    public final static String BROADCAST_ACTION = "com.information.showinformation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onListFragmentInteraction(InformationModel item) {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }
}
