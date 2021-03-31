Codes to reproduce https://issuetracker.google.com/issues/183986649.

### Build and Install
```
 ./gradlew build
adb install app/build/outputs/apk/debug/app-debug.apk
```


### How to reproduce the issue
Pixel 3 (Android 11, RQ2A.210305.006)

1. Launch Issue183986649 app from the launcher.
2. Choose "While using the app" in the permission dialog.
3. The activity starts LocationService. And then you can see the location updates in the logcat. It is expected behavior. e.g.

```
2021-03-31 17:36:37.458 29752-29752/com.example.issue183986649 D/locationService: onLocationResult, Location[fused 35.690048,139.648743 hAcc=16 et=+23h20m55s579ms alt=72.68766004699852 vAcc=3 sAcc=??? bAcc=??? {Bundle[mParcelledData.dataSize=52]}]
2021-03-31 17:36:43.021 29752-29752/com.example.issue183986649 D/locationService: onLocationResult, Location[fused 35.690032,139.648735 hAcc=14 et=+23h21m0s611ms alt=72.91376574563863 vel=0.00994839 bear=203.43661 vAcc=3 sAcc=??? bAcc=??? {Bundle[mParcelledData.dataSize=52]}]
2021-03-31 17:36:44.218 29752-29752/com.example.issue183986649 D/locationService: onLocationResult, Location[fused 35.690031,139.648734 hAcc=10 et=+23h21m3s287ms alt=72.8685127241304 vel=0.01276583 bear=217.34978 vAcc=3 sAcc=2 bAcc=??? {Bundle[mParcelledData.dataSize=52]}]
```

4. Tap back to exit the app.
5. Connect a Bluetooth device to the Android device.
6. LocationService is launched by ACL_CONNECT event. However, location updates are not seen in the logcat. It is the issue in this ticket.   e.g.

```
2021-03-31 17:40:49.150 29752-29752/com.example.issue183986649 D/locationService: onStartCommand()
2021-03-31 17:40:49.156 29752-29752/com.example.issue183986649 D/locationService: checkLastLocation
2021-03-31 17:40:49.156 29752-29752/com.example.issue183986649 D/locationService: startLocationUpdate
2021-03-31 17:40:49.163 29752-29752/com.example.issue183986649 W/locationService: Failed to get last location.
```
