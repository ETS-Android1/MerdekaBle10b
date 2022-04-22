package com.example.merdekable.operation;

import android.bluetooth.BluetoothGatt;

/**
 * This is the superclass of all the operations. This abstract class have abstract method and gatt to
 * enforce the children class have the similar method
 */

public abstract class Operation {

    protected BluetoothGatt gatt;
    public Operation(){

    }

    public Operation(BluetoothGatt gatt){
        this.gatt = gatt;
    }

    public abstract void performOperation();
}
