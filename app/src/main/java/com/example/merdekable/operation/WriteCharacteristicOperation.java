package com.example.merdekable.operation;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;

/**
 * This class is the operation for the server to able to write its local characteristic.
 */
public class WriteCharacteristicOperation extends Operation{

    private BluetoothGattServer mGattServer;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic characteristic;

    public WriteCharacteristicOperation(BluetoothGattServer mGattServer, BluetoothGattCharacteristic characteristic, BluetoothDevice device){
        this.mGattServer = mGattServer;
        this.device = device;
        this.characteristic = characteristic;
    }

    @Override
    public void performOperation() {
        mGattServer.notifyCharacteristicChanged(device, characteristic, false);
    }
}
