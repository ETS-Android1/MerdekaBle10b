package com.example.merdekable.blueooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.merdekable.Constants;
import com.example.merdekable.MerdekaConnect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Scanner {

    private BluetoothLeScanner scanner = null;
    private BluetoothAdapter bluetooth_adapter = null;
    private Handler handler = new Handler();
    private ScanResultsConsumer scan_results_consumer;
    private Context context;
    private boolean scanning=false;
    private Advertiser bleAdvertiser;
    private Boolean checkScan = false;


    /**
     * Scanner constructor
     * We check whether bluetooth is available & on or not, if it is not, will prompt the user to enable
     * @param context environment, which refers to the device.
     */
    public Scanner(Context context) {
        this.context = context;
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetooth_adapter = bluetoothManager.getAdapter();

        // We use BluetoothAdapter to check whether or not Bluetooth is currently switched on
        if (bluetooth_adapter == null || !bluetooth_adapter.isEnabled()) {
            Log.d(Constants.TAG, "Bluetooth is OFF");
            Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
        }
        Log.d(Constants.TAG, "Bluetooth is ON");
    }


    /**
     * This method allow other object to initiate bluetooth scan
     * @param scan_results_consumer ScanResultsConsumer object
     * @param stop_after_ms time to stop scanning in millisecond
     */
    public void startScanning(final ScanResultsConsumer scan_results_consumer, long stop_after_ms) {
        if (scanning) {
            Log.d(Constants.TAG, "Device is already in scanning so ignoring this request");
            return;
        }
        if (scanner == null) {
            scanner = bluetooth_adapter.getBluetoothLeScanner();
            Log.d(Constants.TAG, "BluetoothScanner object created");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scanning) {
                    Log.d(Constants.TAG, "Stopping scanning");
                    scanner.stopScan(scan_callback);
                    setScanning(false);

                    // advertising
                    if(checkScan != Boolean.TRUE) {
                        bleAdvertiser = new Advertiser(context);
                        bleAdvertiser.updateResultConsumer(scan_results_consumer);

                        // random time will pass in here, so after a random time,device will go into
                        // advertise mode
                        try {
                            TimeUnit.MILLISECONDS.sleep(random());
                            bleAdvertiser.startAdvertising();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else{
                        checkScan = false;
                    }
                } }
        }, stop_after_ms);

        this.scan_results_consumer = scan_results_consumer;
        Log.d(Constants.TAG,"Scanning");

        List<ScanFilter> filters;
        filters = new ArrayList<ScanFilter>();

        ScanFilter scanFilter =  new ScanFilter.Builder().setServiceUuid(new ParcelUuid(MerdekaConnect.getInstance().devicesWanted())).build();
        filters.add(scanFilter);

        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        setScanning(true);
        scanner.startScan(filters, settings, scan_callback);
    }

    /**
     * This method allow other object to stop bluetooth scan
     */
    public void stopScanning() {
        setScanning(false);
        Log.d(Constants.TAG, "Stopping scanning");
        scanner.stopScan(scan_callback);
    }

    /**
     * Bluetooth LE scan callbacks. Scan results are reported using these callbacks.
     */
    private ScanCallback scan_callback = new ScanCallback() {
        public void onScanResult(int callbackType, final ScanResult result) {
            checkScan = true;
            if (!scanning) {
                return;
            }
            scan_results_consumer.candidateDevice(result.getDevice(), result.getScanRecord().getBytes(), result.getRssi());
        }
    };

    /**
     * A method to check the scanning status of the scanner.
     * @return boolean value of whether the scanner is scanning
     */
    public boolean isScanning() {
        return scanning;
    }

    /**
     * A method to set boolean value of scanning
     * @param scanning  boolean value representing the status of scanning
     */
    void setScanning(boolean scanning) {
        this.scanning = scanning;
        if (!scanning) {
            scan_results_consumer.scanningStopped();
        } else {
            scan_results_consumer.scanningStarted();
        }
    }

    /**
     * This method will random generate a time to set the advertiser time in millisecond
     * @return random generated time to set the advertiser time
     */
    public static int random(){
        int max = 3000;              // 0.1 sec
        int min = 100;             // 3.0 sec
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        return random_int;
    }

} // end of Scanner class