package com.example.merdekable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * This is WifiDirectBroadcastReceiver class which lets user do wifi setting.
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MerdekaSetting activity;

    /**
     * WifiDirectBroadcastReceiver constructor to initialise values.
     * @param activity MerdekaSetting object
     * @param manager WifiP2pManager object
     * @param channel WifiP2pManager Channel object
     */
    public WifiDirectBroadcastReceiver(MerdekaSetting activity, WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.activity = activity;
        this.manager = manager;
        this.channel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {

        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {

        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {

        }
    }
}
