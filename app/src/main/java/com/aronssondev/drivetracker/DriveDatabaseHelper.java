package com.aronssondev.drivetracker;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.Date;

public class DriveDatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "drives.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_DRIVE = "drive";
    private static final String COLUMN_DRIVE_ID = "_id";
    private static final String COLUMN_DRIVE_START_DATE = "start_date";

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOCATION_ID = "_id";
    private static final String COLUMN_LOCATION_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOCATION_LATITUDE = "latitude";
    private static final String COLUMN_LOCATION_LONGITUDE = "longitude";
    private static final String COLUMN_LOCATION_ALTITUDE = "altitude";
    private static final String COLUMN_LOCATION_PROVIDER = "provider";
    private static final String COLUMN_LOCATION_DRIVE_ID = "drive_id";

    private static final String CREATE_TABLE_DRIVE =
            "CREATE TABLE " + TABLE_DRIVE + " (" +
                    COLUMN_DRIVE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DRIVE_START_DATE + " INTEGER" + ")";

    private static final String CREATE_TABLE_LOCATION =
            "CREATE TABLE " + TABLE_LOCATION + " (" +
                    COLUMN_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LOCATION_TIMESTAMP + " INTEGER, " +
                    COLUMN_LOCATION_LATITUDE + " REAL, " +
                    COLUMN_LOCATION_LONGITUDE + " REAL, " +
                    COLUMN_LOCATION_ALTITUDE + " REAL, " +
                    COLUMN_LOCATION_PROVIDER + " VARCHAR(100), " +
                    COLUMN_LOCATION_DRIVE_ID + " INTEGER REFERENCES drive(_id)" + ")";

    public DriveDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DRIVE);
        db.execSQL(CREATE_TABLE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertDrive(Drive drive) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DRIVE_START_DATE, drive.getStartDate().getTime());

        return getWritableDatabase().insert(TABLE_DRIVE, null, contentValues);
    }

    public long insertLocation(long driveId, Location location) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LOCATION_TIMESTAMP, location.getTime());
        contentValues.put(COLUMN_LOCATION_LATITUDE, location.getLatitude());
        contentValues.put(COLUMN_LOCATION_LONGITUDE, location.getLongitude());
        contentValues.put(COLUMN_LOCATION_ALTITUDE, location.getAltitude());
        contentValues.put(COLUMN_LOCATION_PROVIDER, location.getProvider());
        contentValues.put(COLUMN_LOCATION_DRIVE_ID, driveId);

        return getWritableDatabase().insert(TABLE_LOCATION, null, contentValues);
    }

    public int deleteDrive(long driveId) {
        return getWritableDatabase().delete(TABLE_DRIVE,
                COLUMN_DRIVE_ID + " = ?", new String[] {driveId + ""});
    }

    public int deleteLocation(long driveId) {
        return getWritableDatabase().delete(TABLE_LOCATION,
                COLUMN_LOCATION_DRIVE_ID + " = ?", new String[] {driveId + ""});
    }

    public DriveCursor queryDrives() {
        Cursor cursor = getReadableDatabase().query(
                TABLE_DRIVE,
                null,
                null,
                null,
                null,
                null,
                COLUMN_DRIVE_START_DATE + " ASC");

        return new DriveCursor(cursor);
    }

    public DriveCursor queryDrive(long driveId) {
        Cursor cursor = getReadableDatabase().query(
                TABLE_DRIVE,
                null,
                COLUMN_DRIVE_ID + " = ?",
                new String[] {driveId + ""},
                null,
                null,
                null,
                "1");

        return new DriveCursor(cursor);
    }

    public LocationCursor queryLocationsForDrive(long driveId) {
        Cursor cursor = getReadableDatabase().query(
                TABLE_LOCATION,
                null,
                COLUMN_LOCATION_DRIVE_ID + " = ?",
                new String[] {driveId + ""},
                null,
                null,
                COLUMN_LOCATION_TIMESTAMP + " ASC");

        return new LocationCursor(cursor);
    }

    public LocationCursor queryLastLocationForDrive(long driveId) {
        Cursor cursor = getReadableDatabase().query(
                TABLE_LOCATION,
                null,
                COLUMN_LOCATION_DRIVE_ID + " = ?",
                new String[] {driveId + ""},
                null,
                null,
                COLUMN_LOCATION_TIMESTAMP + " DESC",
                "1");

        return new LocationCursor(cursor);
    }

    public static class DriveCursor extends CursorWrapper {

        public DriveCursor(Cursor cursor) {
            super(cursor);
        }

        public Drive getDrive() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }

            Drive drive = new Drive();

            long driveId = getLong(getColumnIndex(COLUMN_DRIVE_ID));
            drive.setId(driveId);

            long startDate = getLong(getColumnIndex(COLUMN_DRIVE_START_DATE));
            drive.setStartDate(new Date(startDate));

            return drive;
        }
    }

    public static class LocationCursor extends CursorWrapper {

        public LocationCursor(Cursor cursor) {
            super(cursor);
        }

        public Location getLocation() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }

            String provider = getString(getColumnIndex(COLUMN_LOCATION_PROVIDER));

            Location location = new Location(provider);

            long time = getLong(getColumnIndex(COLUMN_LOCATION_TIMESTAMP));
            location.setTime(time);

            double latitude = getDouble(getColumnIndex(COLUMN_LOCATION_LATITUDE));
            location.setLatitude(latitude);

            double longitude = getDouble(getColumnIndex(COLUMN_LOCATION_LONGITUDE));
            location.setLongitude(longitude);

            double altitude = getDouble(getColumnIndex(COLUMN_LOCATION_ALTITUDE));
            location.setAltitude(altitude);

            return location;
        }
    }
}
