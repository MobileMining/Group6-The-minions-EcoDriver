package com.gu.gminions.ecodriver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.gu.gminions.db.*;

import java.util.List;

public class DriveManager {

    private static final String TAG = "DriveManager";

    public static final String PREFS_FILE = "drives";
    public static final String PREF_CURRENT_TRIP_ID = "DriveManager.currentTripId";

    public static final String ACTION_LOCATION =
            "com.gu.gminions.ecodriver.ACTION_LOCATION";

    private static final String TEST_PROVIDER = "TEST_PROVIDER";

    private static DriveManager sDriveManager;

    private Context mAppContext;

    private LocationManager mLocationManager;

    private DriveDatabaseHelper mDriveDatabaseHelper;
    private DriveDataSource mDataSource;

    private SharedPreferences mSharedPreferences;
    private long mCurrentTripId;

    private boolean mAllTripCacheSynced;
    private List<Trip> mAllTripCache;

    private DriveManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);

        mDriveDatabaseHelper = new DriveDatabaseHelper(mAppContext);
        mDataSource = new DriveDataSource(mAppContext);

        mSharedPreferences = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        mAllTripCacheSynced = false;
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

    public boolean isTrackingTrip() {
        return getLocationPendingIntent(false) != null;
    }

    public boolean isTrackingTrip(Trip trip) {
        return trip != null && trip.getId() == mCurrentTripId;
    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public Trip startTrackingTrip(Trip trip) {
        mAllTripCacheSynced = false;

        if (trip == null) {
            trip = createTrip();
        }
        mCurrentTripId = trip.getId();
        mSharedPreferences.edit().putLong(PREF_CURRENT_TRIP_ID, mCurrentTripId).commit();
        startLocationUpdates();

        return trip;
    }

    public void stopTrackingTrip() {
        mAllTripCacheSynced = false;

        stopLocationUpdates();
        mSharedPreferences.edit().remove(PREF_CURRENT_TRIP_ID).commit();
        mCurrentTripId = 0;
    }

    private Trip createTrip() {
        mAllTripCacheSynced = false;

        Trip trip= new Trip();
        mDataSource.createTrip(trip);
        return trip;
    }

    private void deleteTrip(long tripId) {
        mAllTripCacheSynced = false;

        // remove trip from db
        mDataSource.deleteTrip(tripId);

        // TODO: remove all trip records from db
    }

    public List<Trip> getAllTrips() {
        if(!mAllTripCacheSynced || mAllTripCache == null) {
            mAllTripCache = mDataSource.getAllTrips();
            mAllTripCacheSynced = true;
        }
        return mAllTripCache;
    }



    /* TODO: change all to record

   public Trip getTrip(long tripId) {
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
        if (isTrackingTrip()) {
            mDriveDatabaseHelper.insertLocation(mCurrentTripId, location);
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
    */
}
