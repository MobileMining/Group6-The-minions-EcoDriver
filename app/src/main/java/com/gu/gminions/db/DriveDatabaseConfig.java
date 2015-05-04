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
    static final String TABLE_RECORD = "record";

    // table_trip
    static final String COLUMN_TRIP_ID = "trip_id";
    static final String COLUMN_TRIP_STARTTIME = "startTime";
    static final String COLUMN_TRIP_ENDTIME = "endTime";
    static final String COLUMN_TRIP_STARTMILEAGE = "startMileage";
    static final String COLUMN_TRIP_ENDMILEAGE = "endMileage";
    static final String COLUMN_TRIP_FUEL = "fuelConsume";
    static final String COLUMN_TRIP_RATING = "rating";

    static final String[] COLUMNS_TRIP = {
            COLUMN_TRIP_ID,
            COLUMN_TRIP_STARTTIME,
            COLUMN_TRIP_ENDTIME,
            COLUMN_TRIP_STARTMILEAGE,
            COLUMN_TRIP_ENDMILEAGE,
            COLUMN_TRIP_FUEL,
            COLUMN_TRIP_RATING,
    };

    // table_record
    static final String COLUMN_RECORD_ID = "record_id";
    static final String COLUMN_RECORD_TRIPID = "trip_id";
    static final String COLUMN_RECORD_STARTTIME = "startTime";
    static final String COLUMN_RECORD_ENDTIME = "endTime";
    static final String COLUMN_RECORD_SPEED = "speed";
    static final String COLUMN_RECORD_RPM = "RPM";
    static final String COLUMN_RECORD_FUEL = "fuelConsume";
    static final String COLUMN_RECORD_TIMESTAMP = "timestamp";
    static final String COLUMN_RECORD_LATITUDE = "latitude";
    static final String COLUMN_RECORD_LONGITUDE = "longitude";
    static final String COLUMN_RECORD_ALTITUDE = "altitude";
    static final String COLUMN_RECORD_PROVIDER = "provider";

    static final String[] COLUMNS_RECORD = {
            COLUMN_RECORD_ID,
            COLUMN_RECORD_TRIPID,
            COLUMN_RECORD_STARTTIME,
            COLUMN_RECORD_ENDTIME,
            COLUMN_RECORD_SPEED,
            COLUMN_RECORD_RPM,
            COLUMN_RECORD_FUEL,
            COLUMN_RECORD_TIMESTAMP,
            COLUMN_RECORD_LATITUDE,
            COLUMN_RECORD_LONGITUDE,
            COLUMN_RECORD_ALTITUDE,
            COLUMN_RECORD_PROVIDER,
    };

    static final String CREATE_TABLE_TRIP = "create table " + TABLE_TRIP + "("
            + COLUMN_TRIP_ID + " integer primary key autoincrement, "
            + COLUMN_TRIP_STARTTIME + " text not null, "
            + COLUMN_TRIP_ENDTIME + " text, "
            + COLUMN_TRIP_STARTMILEAGE + " integer, "
            + COLUMN_TRIP_ENDMILEAGE + " integer, "
            + COLUMN_TRIP_FUEL + " integer, "
            + COLUMN_TRIP_RATING + " integer);";

    static final String CREATE_TABLE_RECORD = "create table " + TABLE_RECORD + "("
            + COLUMN_RECORD_ID + " integer primary key autoincrement, "
            + COLUMN_RECORD_TRIPID + " integer, "
            + COLUMN_RECORD_STARTTIME + " text not null, "
            + COLUMN_RECORD_ENDTIME + " text not null, "
            + COLUMN_RECORD_SPEED + " real, "
            + COLUMN_RECORD_RPM + " integer, "
            + COLUMN_RECORD_FUEL + " integer, "
            + COLUMN_RECORD_TIMESTAMP + " integer, "
            + COLUMN_RECORD_LATITUDE + " real, "
            + COLUMN_RECORD_LONGITUDE + " real, "
            + COLUMN_RECORD_ALTITUDE + " real, "
            + COLUMN_RECORD_PROVIDER + " varchar(100));";
}
