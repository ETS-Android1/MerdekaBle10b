package com.example.merdekable.blueooth;

import android.bluetooth.BluetoothDevice;

/**
 *   ScanResultsConsumer interface
 *   This interface will be implemented in MerdekaConnect.java so that it can receive and process data
 *   produced by the ble scanning
 */
public interface ScanResultsConsumer {

    public void candidateDevice(BluetoothDevice device, byte[] scan_record, int rssi);
    public void scanningStarted();
    public void scanningStopped();

}


