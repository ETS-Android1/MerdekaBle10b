package com.example.merdekable.blueooth;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.example.merdekable.Constants.TAG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.merdekable.Constants;
import com.example.merdekable.MerdekaConnect;
import com.example.merdekable.operation.OperationManager;
import com.example.merdekable.operation.WriteCharacteristicOperation;

import java.util.ArrayList;

/**
 * This is an wrapper class for advertising activity. Initially, the device will be a scanner, after a random
 * second, the device will turn into advertiser.
 */
public class Advertiser {

    private OperationManager operationManager = new OperationManager();
    private BluetoothLeAdvertiser advertiser;
    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mGattServer;
    private Context context;
    private boolean advertising =  false;
    private static ArrayList<BluetoothDevice> mDevices = new ArrayList();
    private Handler handler = new Handler();
    private ScanResultsConsumer scan_results_consumer;

    /**
     * This is Advertiser constructor, to set up BLE components
     * @param context environment, which refer to the device
     */
    public Advertiser(Context context){
        this.context = context;
        advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        GattServerCallback gattServerCallback = new GattServerCallback();
        mGattServer = mBluetoothManager.openGattServer(context, gattServerCallback);
        setupServer();
    }

    /**
     * A method to pass the interface to the MainActivity class. This method will be used to update the user
     * interface if there is any update from Advertiser.
     * @param src ScanResultsConsumer object
     */
    public void updateResultConsumer(ScanResultsConsumer src){
        scan_results_consumer = src;
    }


    /**
     * This method is called when initialise the advertiser. Here we define the server we want to advertise,
     * set up the server for advertiser to advertises and server's settings will be set here as well.
     */
    private void setupServer(){
        BluetoothGattService service = new BluetoothGattService(MerdekaConnect.
                getInstance().devicesWanted(),BluetoothGattService.SERVICE_TYPE_PRIMARY);
        //write
        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                Constants.CHARACTERISTIC_ECHO_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(writeCharacteristic);
        mGattServer.addService(service);
    }


    /**
     * This method is called to let advertiser to start advertising.
     * MainActivity class can call this method to let the advertiser to start advertising.
     */
    public void startAdvertising(){
        if(advertising){
            Log.d(Constants.TAG, "Already Advertising");
            return;
        }
        if(advertiser == null){
            Log.d(Constants.TAG, "Bluetooth Advertiser Failed");
            return;
        }
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                // Low latency used here to maximize our advertising quality
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable(true)
                .build();

        ParcelUuid pUuid = new ParcelUuid(MerdekaConnect.getInstance().devicesWanted());
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( false )
                .addServiceUuid( pUuid )
                .build();


        advertiser.startAdvertising( settings, data, advertisingCallback );
        setAdvertising(true);

    }

    /**
     * This object will define what happen if advertising is failed or success.
     * This is a callback so when advertising is successful, any result from the advertising
     * will be handled.
     */
    private AdvertiseCallback advertisingCallback = new AdvertiseCallback() {

        /**
         * This method will be called if the advertising is successful, logs is to confirm the the device
         * is advertising successfully.
         * @param settingsInEffect AdvertiseSettings object
         */
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d(Constants.TAG,"Advertise Successful");
        }


        /**
         * This method will be called if the advertising has failed, error code is printed as well so we
         * know the reason if the device failed to advertise.
         * @param errorCode error code message
         */
        @Override
        public void onStartFailure(int errorCode) {
            Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
            super.onStartFailure(errorCode);
        }
    };

    /**
     * stopAdvertising method can be called to make the advertiser stop advertise.
     */
    public void stopAdvertising(){
        setAdvertising(false);
        advertiser.stopAdvertising(advertisingCallback);
    }

    /**
     * A boolean method which check whether the advertiser is advertising or not,
     * return false if the advertiser is not advertising, else true.
     * @return boolean value of whether advertiser is advertising or not
     */
    public boolean isAdvertising(){
        return advertising;
    }

    /**
     * This method is to set whether the advertiser is advertising or not
     * @param advertising boolean value of whether the advertiser is advertising or not
     */
    void setAdvertising(boolean advertising){
        this.advertising = advertising;
    }

    /**
     * An object to handled all the events in Gatt connection after the advertiser connected by a scanner.
     */
    private class GattServerCallback extends BluetoothGattServerCallback {

        /**
         * A remote client has requested to write a local characteristic.
         * @param device remote device that has requested the write operation
         * @param requestId Id of the request
         * @param characteristic characteristic to be written to
         * @param preparedWrite  boolean value of whether this write operation should be queued for later execution
         * @param responseNeeded  boolean value of whether the remote device requires a response
         * @param offset offset given for the value
         * @param value value the client wants to assign to the characteristic
         */
        public void onCharacteristicWriteRequest(BluetoothDevice device,
                                                 int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite,
                                                 boolean responseNeeded,
                                                 int offset,
                                                 byte[] value) {
            super.onCharacteristicWriteRequest(device,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value);
            //advertiser will see the number of connected device and notify every phone.
            Log.i(TAG,"inside on characterisitic write request in advertiser");

            if (characteristic.getUuid().equals(Constants.CHARACTERISTIC_ECHO_UUID)) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                int num_connected = mDevices.size() + 1;

                for(BluetoothDevice dev : mDevices) {
                    operationManager.request(new WriteCharacteristicOperation(mGattServer, characteristic, dev));
                }
            }
        }
    }

}







