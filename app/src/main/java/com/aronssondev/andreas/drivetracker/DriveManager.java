package com.aronssondev.andreas.drivetracker;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.aronssondev.andreas.drivetracker.DriveDatabaseHelper.LocationCursor;
import com.aronssondev.andreas.drivetracker.DriveDatabaseHelper.DriveCursor;

public class DriveManager {
    private static final String TAG = "DriveManager";

    private static final String PREFS_FILE = "drives";
    private static final String PREF_CURRENT_RUN_ID = "DriveManager.currentDriveId";

    public static final String ACTION_LOCATION = "com.aronssondev.andreas.drivetracker.ACTION_LOCATION";
    
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    
    private static DriveManager sDriveManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private DriveDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentDriveId;
    
    private DriveManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new DriveDatabaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentDriveId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }
    
    public static DriveManager get(Context c) {
        if (sDriveManager == null) {
            // we use the application context to avoid leaking activities
            sDriveManager = new DriveManager(c.getApplicationContext());
        }
        return sDriveManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
        // if we have the test provider and it's enabled, use it
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && 
                mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider " + provider);

        // get the last known location and broadcast it if we have one
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            // reset the time to now
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
        // start updates from the location manager
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }
    
    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    
    public boolean isTrackingDrive() {
        return getLocationPendingIntent(false) != null;
    }
    
    public boolean isTrackingDrive(Drive drive) {
        return drive != null && drive.getId() == mCurrentDriveId;
    }
    
    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }
    
    public Drive startNewDrive() {
        // insert a drive into the db
        Drive drive = insertDrive();
        // start tracking the drive
        startTrackingDrive(drive);
        return drive;
    }
    
    public void startTrackingDrive(Drive drive) {
        // keep the ID
        mCurrentDriveId = drive.getId();
        // store it in shared preferences
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentDriveId).commit();
        // start location updates
        startLocationUpdates();
    }
    
    public void stopDrive() {
        stopLocationUpdates();
        mCurrentDriveId = -1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }
    
    private Drive insertDrive() {
        Drive drive = new Drive();
        drive.setId(mHelper.insertDrive(drive));
        return drive;
    }

    public DriveCursor queryDrives() {
        return mHelper.queryDrives();
    }
    
    public Drive getDrive(long id) {
        Drive drive = null;
        DriveCursor cursor = mHelper.queryDrive(id);
        cursor.moveToFirst();
        // if we got a row, get a drive
        if (!cursor.isAfterLast())
            drive = cursor.getDrive();
        cursor.close();
        return drive;
    }

    public void insertLocation(Location loc) {
        if (mCurrentDriveId != -1) {
            mHelper.insertLocation(mCurrentDriveId, loc);
        } else {
            Log.e(TAG, "Location received with no tracking drive; ignoring.");
        }
    }
    
    public Location getLastLocationForDrive(long driveId) {
        Location location = null;
        LocationCursor cursor = mHelper.queryLastLocationForDrive(driveId);
        cursor.moveToFirst();
        // if we got a row, get a location
        if (!cursor.isAfterLast())
            location = cursor.getLocation();
        cursor.close();
        return location;
    }

    public LocationCursor queryLocationsForDrive(long driveId) {
        return mHelper.queryLocationsForDrive(driveId);
    }

}
