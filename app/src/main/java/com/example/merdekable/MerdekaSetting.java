package com.example.merdekable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is MerdekaSetting class which lets users to do Bluetooth, Wifi and Location settings.
 */
public class MerdekaSetting extends AppCompatActivity {

    private Button LocBtn;
    private LocationRequest locationRequest;
    public static final int REQUEST_CHECK_SETTING = 1001;
    Button WifiBtn;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    Switch sw;
    TextView textview;
    BluetoothAdapter bt;
    int i = 1;
    Intent bluetoothIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merdeka_settings);

        //Turning On Location
        LocBtn = findViewById(R.id.locationbutton);
        LocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(2000);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);

                Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());

                result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                            Toast.makeText(MerdekaSetting.this, "Location is On", Toast.LENGTH_SHORT).show();

                        } catch (ApiException e) {

                            switch (e.getStatusCode()) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                        resolvableApiException.startResolutionForResult(MerdekaSetting.this, REQUEST_CHECK_SETTING);
                                    } catch (IntentSender.SendIntentException sendIntentException) {

                                    }
                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }

                    }
                });

            }
        });

        //Turn On & Off Wifi
        initialWork();
        exqListener();

        sw = (Switch) findViewById(R.id.switch1);
        textview = (TextView) findViewById(R.id.textView1);

        bt = BluetoothAdapter.getDefaultAdapter();
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    BluetoothEnable();
                } else {
                    BluetoothDisable();
                }
            }
        });
    }

    /**
     * This method turns on bluetooth.
     */
    public void BluetoothEnable() {
        bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(bluetoothIntent, i);
        textview.setText("Bluetooth ON");
    }

    /**
     * This method turns off bluetooth.
     */
    public void BluetoothDisable() {
            bt.disable();
            textview.setText("Bluetooth OFF");
    }

    /**
     * This method tells whether Location is turned on or not.
     * @param requestCode request code
     * @param resultCode result code
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHECK_SETTING){

            switch (resultCode){

                case Activity.RESULT_OK:
                    Toast.makeText(this, "Location is Turned On", Toast.LENGTH_SHORT).show();
                    break;

                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Location is required to be turn On", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method brings users to wifi setting page.
     */
    private void exqListener()
    {
        WifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(intent,1 );
            }
        });
    }

    /**
     * This method adds wifi actions.
     */
    private void initialWork()
    {
        WifiBtn = findViewById(R.id.wifibutton);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WifiDirectBroadcastReceiver(this, manager, channel);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

    }

    /**
     * This method registers receiver.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(receiver,intentFilter);
    }

    /**
     * This method unregisters receiver.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}