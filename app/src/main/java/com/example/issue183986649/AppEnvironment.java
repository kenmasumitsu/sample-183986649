package com.example.issue183986649;

import android.content.Context;
import android.content.Intent;

public enum AppEnvironment {
    INSTANCE;

    void startService(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        context.startForegroundService(intent);
    }

    void stopService(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        context.stopService(intent);
    }
}
