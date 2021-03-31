package com.example.issue183986649;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationService extends Service {
    static final String TAG = "locationService";
    private static final String NOTIFICATION_CHANNEL_ID = "com.example.issue183986649.channel";

    IMyService.Stub mBinder = new IMyService.Stub() {
        @Override
        public void sayHello() throws RemoteException {
            Log.d(TAG, "hello world!");
        }
    };

    private Location mLocation = null;
    private FusedLocationProviderClient mFusedLocationClient;

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "onLocationResult, " + locationResult.getLastLocation());
            onUpdateLocation(locationResult.getLastLocation());
        }
    };

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_NONE);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        // setup notification
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("title")
                .setContentText("text")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
        startForeground(1, notification);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "no permission");
            return START_NOT_STICKY;
        }

        // setup FuseLocationProvider
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLastLocation();
        startLocationUpdate();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved()");
        stopLocationUpadte();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        Log.d(TAG, "startLocationUpdate");
        mFusedLocationClient.requestLocationUpdates(
                createLocationRequest(), mLocationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpadte() {
        Log.d(TAG, "stopLocationUpadte");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    private void checkLastLocation() {
        Log.d(TAG, "checkLastLocation");
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {

                            if (task.isSuccessful() && task.getResult() != null) {
                                Location location = task.getResult();
                                Log.w(TAG, "Got the last location. " + location);
                                onUpdateLocation(location);
                            } else {
                                Log.w(TAG, "Failed to get last location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onUpdateLocation(Location location) {
        //Log.d(TAG, "onLocationUpdated, " + location);
        mLocation = location;
    }
}