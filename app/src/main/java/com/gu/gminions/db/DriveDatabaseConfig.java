package com.gu.gminions.db;

/**
 * Created by jied on 5/4/15.
 */
public class DriveDatabaseConfig {
    // db
    static final String DATABASE_NAME = "ecodrive.db";
    static final int DATABASE_VERSION = 1;

    // tables
    static final String TABLE_TRIP = "trip";
    static final String TABLE_WARNING = "warning";

    // table_trip
    static final String COLUMN_TRIP_ID = "trip_id";
    static final String COLUMN_TRIP_STARTTIME = "startTime";
    static final String COLUMN_TRIP_ENDTIME = "endTime";
    static final String COLUMN_TRIP_STARTMILEAGE = "startMileage";
    static final String COLUMN_TRIP_ENDMILEAGE = "endMileage";
    static final String COLUMN_TRIP_FUEL = "fuelConsume";
    static final String COLUMN_TRIP_WARNING = "warning";
    static final String COLUMN_TRIP_RATING = "rating";

    static final String[] COLUMNS_TRIP = {
            COLUMN_TRIP_ID,
            COLUMN_TRIP_STARTTIME,
            COLUMN_TRIP_ENDTIME,
            COLUMN_TRIP_STARTMILEAGE,
            COLUMN_TRIP_ENDMILEAGE,
            COLUMN_TRIP_FUEL,
            COLUMN_TRIP_WARNING,
            COLUMN_TRIP_RATING,
    };

    // table_warning
    static final String COLUMN_WARNING_ID = "warning_id";
    static final String COLUMN_WARNING_TRIPID = "trip_id";
    static final String COLUMN_WARNING_TIME = "time";
    static final String COLUMN_WARNING_SPEED = "speed";
    //static final String COLUMN_RECORD_RPM = "RPM";
    //static final String COLUMN_RECORD_FUEL = "fuelConsume";
    //static final String COLUMN_RECORD_TIMESTAMP = "timestamp";
    static final String COLUMN_WARNING_LATITUDE = "latitude";
    static final String COLUMN_WARNING_LONGITUDE = "longitude";
    static final String COLUMN_WARNING_ALTITUDE = "altitude";
    static final String COLUMN_WARNING_TYPE = "type";

    static final String[] COLUMNS_WARNING = {
            COLUMN_WARNING_ID,
            COLUMN_WARNING_TRIPID,
            COLUMN_WARNING_TIME,
            COLUMN_WARNING_SPEED,
            //COLUMN_RECORD_RPM,
            //COLUMN_RECORD_FUEL,
            //COLUMN_RECORD_TIMESTAMP,
            COLUMN_WARNING_LATITUDE,
            COLUMN_WARNING_LONGITUDE,
            COLUMN_WARNING_ALTITUDE,
            COLUMN_WARNING_TYPE,
    };

    static final String CREATE_TABLE_TRIP = "create table " + TABLE_TRIP + "("
            + COLUMN_TRIP_ID + " integer primary key autoincrement, "
            + COLUMN_TRIP_STARTTIME + " text not null, "
            + COLUMN_TRIP_ENDTIME + " text, "
            + COLUMN_TRIP_STARTMILEAGE + " integer, "
            + COLUMN_TRIP_ENDMILEAGE + " integer, "
            + COLUMN_TRIP_FUEL + " integer, "
            + COLUMN_TRIP_WARNING + " integer, "
            + COLUMN_TRIP_RATING + " integer);";

    static final String CREATE_TABLE_WARNING = "create table " + TABLE_WARNING + "("
            + COLUMN_WARNING_ID + " integer primary key autoincrement, "
            + COLUMN_WARNING_TRIPID + " integer, "
            + COLUMN_WARNING_TIME + " text not null, "
            //+ COLUMN_RECORD_ENDTIME + " text not null, "
            + COLUMN_WARNING_SPEED + " real, "
            //+ COLUMN_RECORD_RPM + " integer, "
            //+ COLUMN_RECORD_FUEL + " integer, "
            //+ COLUMN_RECORD_TIMESTAMP + " integer, "
            + COLUMN_WARNING_LATITUDE + " real, "
            + COLUMN_WARNING_LONGITUDE + " real, "
            + COLUMN_WARNING_ALTITUDE + " real, "
            + COLUMN_WARNING_TYPE + " varchar(100));";
}
