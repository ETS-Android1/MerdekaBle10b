package com.example.merdekable;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.merdekable.blueooth.Advertiser;
import com.example.merdekable.blueooth.ScanResultsConsumer;
import com.example.merdekable.blueooth.Scanner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

/**
 * This is MerdekaConnect class to let user press Scanning button to scan for devices nearby
 * and then play sound.
 */
public class MerdekaConnect extends AppCompatActivity implements ScanResultsConsumer {

    private boolean ble_scanning = false;
    private Handler handler = new Handler();
    private Scanner ble_scanner;
    private static final long SCAN_TIMEOUT = 5000;
    private static final int REQUEST_LOCATION = 0;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};
    private boolean permissions_granted = false;
    private int device_count = 0;
    private Toast toast;
    private static MerdekaConnect instance;
    Advertiser advertiser;

    private DatabaseReference node;
    private int deviceCountDb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merdeka_connect);
        instance = this;
        setButtonText();
        node = FirebaseDatabase.getInstance().getReference().child("Device");
        ble_scanner = new Scanner(this.getApplicationContext());
        advertiser = new Advertiser(this.getApplicationContext());
    }

    /**
     * This method sets the text showing on the button.
     */
    private void setButtonText() {
        String text;
        text = Constants.FIND;
        final String button_text = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) MerdekaConnect.this.findViewById(R.id.scanButton)).setText(button_text);
            }
        });
    }

    /**
     * This method changes the text on the scan screen’s button according to whether scanning
     * is currently being performed.
     * If scanning is in process, text is changed to "Stop scanning" and the button is disabled.
     *
     */
    private void setScanState(boolean value) {
        ble_scanning = value;
        ((Button) this.findViewById(R.id.scanButton)).setText(value ? Constants.STOP_SCANNING : Constants.FIND);

        // When scanning is in process, disable button. Else, button is enabled.
        ((Button) this.findViewById(R.id.scanButton)).setEnabled(value ? false : true);

        // Once finish scanning, remove all devices from database and stop advertising
        if (!value) {
            advertiser.stopAdvertising();
            FirebaseDatabase.getInstance().getReference().child("Device").removeValue();
        }

        // Count number of devices in database and then display it on screen
        TextView deviceCountDbStr = this.findViewById(R.id.deviceCountDbStr);
        node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deviceCountDb = (int) dataSnapshot.getChildrenCount();
                deviceCountDbStr.setText("Number of Devices in Database: " + Integer.toString(deviceCountDb));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // obj for sound player
        MediaPlayer mp = MediaPlayer.create(this, R.raw.merdeka);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (deviceCountDb > 1){
                    mp.start();
                    advertiser.stopAdvertising();

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                        public void onCompletion(MediaPlayer mp){
                            mp.release();

                            //remove all devices from database after playing sound
                            FirebaseDatabase.getInstance().getReference().child("Device").removeValue();

                        }
                    }); // end mp release, basically stop the song after it finished
                }
            }
        });

    }

    /**
     * This method will trigger Bluetooth scanning (unless we’re already scanning).
     */
    public void onScan(View view) {
        if (!ble_scanner.isScanning()) {
            device_count = 0;

            /*
            Build.VERSION.SDK_INT is checking the version of Android we're running on and
            if it's greater than or equal to "M" (Android 6),
            a call to the requestLocationPermission method is made.
            */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions_granted = false;
                    requestLocationPermission();
                } else {
                    Log.i(Constants.TAG, "Location permission has already been granted. Starting scanning.");
                    permissions_granted = true;
                }
            } else {
                permissions_granted = true;
            }
            startScanning();
        } else {
            ble_scanner.stopScanning();
        }
    }

    /**
     * This method requests location permission from users.
     */
    private void requestLocationPermission() {
        Log.i(Constants.TAG, "Location permission has NOT yet been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.i(Constants.TAG, "Displaying location permission rationale to provide additional context.");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission Required");
            builder.setMessage("Please grant Location access so this application can perform Bluetooth scanning");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    Log.d(Constants.TAG, "Requesting permissions after explanation");
                    ActivityCompat.requestPermissions(MerdekaConnect.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            Log.i(Constants.TAG, "Received response for location permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Location permission has been granted
                Log.i(Constants.TAG, "Location permission has now been granted. Scanning.....");
                permissions_granted = true;
                if (ble_scanner.isScanning()) {
                    startScanning();
                }
            } else {
                Log.i(Constants.TAG, "Location permission was NOT granted.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void simpleToast(String message, int duration) {
        toast = Toast.makeText(this, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * This method checks that permissions have been granted
     * and then tells the Scanner object to start scanning.
     */
    private void startScanning() {

        if (permissions_granted) {
            simpleToast(Constants.SCANNING, 3000);
            ble_scanner.startScanning(this, SCAN_TIMEOUT);
        } else {
            Log.i(Constants.TAG, "Permission to perform Bluetooth scanning was not yet granted");
        }
    }

    /**
     * This method adds devices to database.
     * @param device BluetoothDevice object
     * @param scan_record record of scanning
     * @param rssi signal strength indicator
     */
    @Override
    public void candidateDevice(BluetoothDevice device, byte[] scan_record, int rssi) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                device_count++;
                if (device_count == 1){
                    FirebaseDatabase.getInstance().getReference().child("Device").push().setValue("new device");
                }
            }
        });
    }

    /**
     * This method sets scan state to true when scanning starts.
     */
    @Override
    public void scanningStarted() {
        setScanState(true);
    }

    /**
     * This method sets scan state to false when scanning stops.
     */
    @Override
    public void scanningStopped() {
        if (toast != null) {
            toast.cancel();
        }
        setScanState(false);
    }

    /**
     * This method uses scan filter.
     * @return UUID
     */
    public UUID devicesWanted(){
        return Constants.SERVICE_UUID;
    }

    /**
     * This method returns MerdekaConnect object.
     * @return MerdekaConnect object
     */
    public static MerdekaConnect getInstance() {
        return instance;
    }
}