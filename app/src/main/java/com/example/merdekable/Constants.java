package com.example.merdekable;

import java.util.UUID;

/**
 * A class storing all the constant values.
 */
public class Constants {

        public static String SERVICE_STRING = "7D2EA28A-F7BD-485A-BD9D-92AD6ECFE931";
        public static UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);

        public static final String TAG = "MerdekaBLE";
        public static final String FIND = "Start Scanning";
        public static final String STOP_SCANNING = "Stop Scanning";
        public static final String SCANNING = "Scanning";

        //write
        public static String CHARACTERISTIC_ECHO_STRING = "7D2EBAAD-F7BD-485A-BD9D-92AD6ECFE93E";
        public static UUID CHARACTERISTIC_ECHO_UUID = UUID.fromString(CHARACTERISTIC_ECHO_STRING);

}
