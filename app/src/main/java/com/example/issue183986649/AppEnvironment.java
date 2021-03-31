package com.example.issue183986649;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public enum AppEnvironment {
    INSTANCE;

    static final String TAG = "AppEnv";

    private IMyService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMyService.Stub.asInterface(service);
            try {
                mService.sayHello();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @SuppressLint("NewApi")
    void startService(Context context) {
        Intent intent = new Intent(context, LocationService.class);

        context.startForegroundService(intent);
        context.bindService(intent, mConnection, 0);
    }

    void stopService(Context context) {
        Intent intent = new Intent(context, LocationService.class);

        context.unbindService(mConnection);
        context.stopService(intent);
    }
}
