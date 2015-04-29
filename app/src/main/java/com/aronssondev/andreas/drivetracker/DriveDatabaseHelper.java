package com.aronssondev.andreas.drivetracker;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class DriveDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "drives.sqlite";
    private static final int VERSION = 1;
    
    private static final String TABLE_RUN = "drive";
    private static final String COLUMN_RUN_ID = "_id";
    private static final String COLUMN_RUN_START_DATE = "start_date";

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOCATION_LATITUDE = "latitude";
    private static final String COLUMN_LOCATION_LONGITUDE = "longitude";
    private static final String COLUMN_LOCATION_ALTITUDE = "altitude";
    private static final String COLUMN_LOCATION_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOCATION_PROVIDER = "provider";
    private static final String COLUMN_LOCATION_RUN_ID = "drive_id";

    public DriveDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the "drive" table
        db.execSQL("create table drive (_id integer primary key autoincrement, start_date integer)");
        // create the "location" table
        db.execSQL("create table location (" +
                " timestamp integer, latitude real, longitude real, altitude real," +
                " provider varchar(100), drive_id integer references drive(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // implement schema changes and data massage here when upgrading
    }
    
    public long insertDrive(Drive drive) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RUN_START_DATE, drive.getStartDate().getTime());
        return getWritableDatabase().insert(TABLE_RUN, null, cv);
    }
    
    public long insertLocation(long driveId, Location location) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_LATITUDE, location.getLatitude());
        cv.put(COLUMN_LOCATION_LONGITUDE, location.getLongitude());
        cv.put(COLUMN_LOCATION_ALTITUDE, location.getAltitude());
        cv.put(COLUMN_LOCATION_TIMESTAMP, location.getTime());
        cv.put(COLUMN_LOCATION_PROVIDER, location.getProvider());
        cv.put(COLUMN_LOCATION_RUN_ID, driveId);
        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    public DriveCursor queryDrives() {
        // equivalent to "select * from drive order by start_date asc"
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN,
                null, null, null, null, null, COLUMN_RUN_START_DATE + " asc");
        return new DriveCursor(wrapped);
    }
    
    public DriveCursor queryDrive(long id) {
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN, 
                null, // all columns 
                COLUMN_RUN_ID + " = ?", // look for a drive ID
                new String[]{ String.valueOf(id) }, // with this value
                null, // group by
                null, // order by
                null, // having
                "1"); // limit 1 row
        return new DriveCursor(wrapped);
    }


    public LocationCursor queryLocationsForDrive(long driveId) {
        Cursor wrapped = getReadableDatabase().query(TABLE_LOCATION,
                null,
                COLUMN_LOCATION_RUN_ID + " = ?", // limit to the given drive
                new String[]{ String.valueOf(driveId) },
                null, // group by
                null, // having
                COLUMN_LOCATION_TIMESTAMP + " asc"); // order by timestamp
        return new LocationCursor(wrapped);
    }

    public LocationCursor queryLastLocationForDrive(long driveId) {
        Cursor wrapped = getReadableDatabase().query(TABLE_LOCATION, 
                null, // all columns 
                COLUMN_LOCATION_RUN_ID + " = ?", // limit to the given drive
                new String[]{ String.valueOf(driveId) },
                null, // group by
                null, // having
                COLUMN_LOCATION_TIMESTAMP + " desc", // order by latest first
                "1"); // limit 1
        return new LocationCursor(wrapped);
    }

    /**
     * A convenience class to wrap a cursor that returns rows from the "drive" table.
     * The {@link getDrive()} method will give you a Drive instance representing the current row.
     */
    public static class DriveCursor extends CursorWrapper {
        
        public DriveCursor(Cursor c) {
            super(c);
        }
        
        /**
         * Returns a Drive object configured for the current row, or null if the current row is invalid.
         */
        public Drive getDrive() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            Drive drive = new Drive();
            drive.setId(getLong(getColumnIndex(COLUMN_RUN_ID)));
            drive.setStartDate(new Date(getLong(getColumnIndex(COLUMN_RUN_START_DATE))));
            return drive;
        }
    }
    
    public static class LocationCursor extends CursorWrapper {
        
        public LocationCursor(Cursor c) {
            super(c);
        }
        
        public Location getLocation() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            // first get the provider out so we can use the constructor
            String provider = getString(getColumnIndex(COLUMN_LOCATION_PROVIDER));
            Location loc = new Location(provider);
            // populate the remaining properties
            loc.setLongitude(getDouble(getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
            loc.setLatitude(getDouble(getColumnIndex(COLUMN_LOCATION_LATITUDE)));
            loc.setAltitude(getDouble(getColumnIndex(COLUMN_LOCATION_ALTITUDE)));
            loc.setTime(getLong(getColumnIndex(COLUMN_LOCATION_TIMESTAMP)));
            return loc;
        }
    }

}
