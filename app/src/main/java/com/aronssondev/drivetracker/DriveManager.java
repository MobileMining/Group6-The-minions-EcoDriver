package com.aronssondev.drivetracker;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class DriveManager {

    private static final String TAG = "DriveManager";

    public static final String PREFS_FILE = "drives";
    public static final String PREF_CURRENT_DRIVE_ID = "DriveManager.currentDriveId";

    public static final String ACTION_LOCATION =
            "com.aronssondev.drivetracker.ACTION_LOCATION";

    private static final String TEST_PROVIDER = "TEST_PROVIDER";

    private static DriveManager sDriveManager;

    private Context mAppContext;

    private LocationManager mLocationManager;

    private DriveDatabaseHelper mDriveDatabaseHelper;
    private SharedPreferences mSharedPreferences;
    private long mCurrentDriveId;

    private DriveManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);

        mDriveDatabaseHelper = new DriveDatabaseHelper(mAppContext);
        mSharedPreferences = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public static DriveManager getInstance(Context context) {
        if (sDriveManager == null) {
            synchronized (DriveManager.class) {
                if (sDriveManager == null) {
                    sDriveManager = new DriveManager(context);
                }
            }
        }
        return sDriveManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;

        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    private void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        if (mLocationManager.getProvider(TEST_PROVIDER) != null &&
                mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }

        Log.d(TAG, "Using provider " + provider);

        PendingIntent pi = getLocationPendingIntent(true);

        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);

        Location lastKnown = mLocationManager.getLastKnownLocation(provider);

        if (lastKnown != null) {
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
    }

    private void stopLocationUpdates() {
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

    public Drive startTrackingDrive(Drive drive) {
        if (drive == null) {
            drive = insertDrive();
        }
        mCurrentDriveId = drive.getId();
        mSharedPreferences.edit().putLong(PREF_CURRENT_DRIVE_ID, mCurrentDriveId).commit();
        startLocationUpdates();

        return drive;
    }

    public void stopTrackingDrive() {
        stopLocationUpdates();
        mSharedPreferences.edit().remove(PREF_CURRENT_DRIVE_ID).commit();
        mCurrentDriveId = 0;
    }

    public void removeDrive(long driveId) {
        deleteLocation(driveId);
        deleteDrive(driveId);
    }

    private Drive insertDrive() {
        Drive drive = new Drive();
        drive.setId(mDriveDatabaseHelper.insertDrive(drive));
        return drive;
    }

    private int deleteDrive(long driveId) {
        return mDriveDatabaseHelper.deleteDrive(driveId);
    }

    public DriveDatabaseHelper.DriveCursor queryDrives() {
        return mDriveDatabaseHelper.queryDrives();
    }

    public Drive getDrive(long driveId) {
        Drive drive = null;
        DriveDatabaseHelper.DriveCursor driveCursor = mDriveDatabaseHelper.queryDrive(driveId);

        driveCursor.moveToFirst();

        if (!driveCursor.isAfterLast()) {
            drive = driveCursor.getDrive();
        }

        driveCursor.close();

        return drive;
    }

    public void insertLocation(Location location) {
        if (isTrackingDrive()) {
            mDriveDatabaseHelper.insertLocation(mCurrentDriveId, location);
        } else {
            Log.e(TAG, "Location received with no tracking drive; ignoring.");
        }
    }

    private int deleteLocation(long driveId) {
        return mDriveDatabaseHelper.deleteLocation(driveId);
    }

    public DriveDatabaseHelper.LocationCursor queryLocationsForDrive(long driveId) {
        return mDriveDatabaseHelper.queryLocationsForDrive(driveId);
    }

    public Location getLastLocationForDrive(long driveId) {
        Location location = null;
        DriveDatabaseHelper.LocationCursor locationCursor=
                mDriveDatabaseHelper.queryLastLocationForDrive(driveId);

        locationCursor.moveToFirst();

        if (!locationCursor.isAfterLast()) {
            location = locationCursor.getLocation();
        }

        locationCursor.close();

        return location;
    }
}
