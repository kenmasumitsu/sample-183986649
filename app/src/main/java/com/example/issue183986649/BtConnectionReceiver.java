package com.example.issue183986649;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BtConnectionReceiver extends BroadcastReceiver {
    static final String TAG = "BtReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "Receive event: " + action);

        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            Log.d(TAG, "Bluetooth ACL Connected! Start Service");
            AppEnvironment.INSTANCE.startService(context.getApplicationContext());
        }
    }
}