package com.gu.gminions.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import static com.gu.gminions.db.DriveDatabaseConfig.*;

/**
 * Created by jied on 01/04/15.
 * DAO, database access object;
 * maintain database connection, supporting add new and fetch all data.
 */
public class DriveDataSource {
    private SQLiteDatabase database;
    private DriveDatabaseHelper dbHelper;

    public DriveDataSource(Context context){
        dbHelper = new DriveDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public long createTrip(Trip trip, List<Warning> warnings, List<Location> locations) {
        // add trip
        ContentValues tripValues = new ContentValues();

        tripValues.put(COLUMN_TRIP_STARTTIME, trip.getStartTime());
        tripValues.put(COLUMN_TRIP_ENDTIME, trip.getEndTime());
        tripValues.put(COLUMN_TRIP_DURATION, trip.getDuration());
        tripValues.put(COLUMN_TRIP_STARTMILEAGE, trip.getStartMileage());
        tripValues.put(COLUMN_TRIP_ENDMILEAGE, trip.getEndMileage());
        tripValues.put(COLUMN_TRIP_FUEL, trip.getFuelConsume());
        tripValues.put(COLUMN_TRIP_WARNING, trip.getTotalWarning());
        tripValues.put(COLUMN_TRIP_RATING, trip.getRating());

        //insert row
        long tripId = database.insert(TABLE_TRIP, null, tripValues);

        /*
        Cursor cursor = database.query(
                TABLE_TRIP,
                COLUMNS_TRIP,
                COLUMN_TRIP_ID + " = " + tripId,
                null,
                null,
                null,
                null);
        */

        trip.setId(tripId);

        // add warnings
        if (warnings != null){
            for(Warning wn : warnings) {
                ContentValues warningValues = new ContentValues();

                warningValues.put(COLUMN_WARNING_TRIPID, tripId);
                warningValues.put(COLUMN_WARNING_TIME, wn.getTime());
                warningValues.put(COLUMN_WARNING_SPEED, wn.getSpeed());
                warningValues.put(COLUMN_WARNING_LATITUDE, wn.getLatitude());
                warningValues.put(COLUMN_WARNING_LONGITUDE, wn.getLongitude());
                warningValues.put(COLUMN_WARNING_ALTITUDE, wn.getAltitude());
                warningValues.put(COLUMN_WARNING_TYPE, wn.getType());

                database.insert(TABLE_WARNING, null, warningValues);
            }
        }

        // add locations
        if (locations != null) {
            for(Location loc : locations) {
                ContentValues locationValues = new ContentValues();

                locationValues.put(COLUMN_LOCATION_TRIPID, tripId);
                locationValues.put(COLUMN_LOCATION_LATITUDE, loc.getLatitude());
                locationValues.put(COLUMN_LOCATION_LONGITUDE, loc.getLongitude());
                locationValues.put(COLUMN_LOCATION_ALTITUDE, loc.getAltitude());

                //insert row
                database.insert(TABLE_LOCATION, null, locationValues);
            }
        }

        return tripId;
    }

    public void deleteTrip(long tripId){
        database.delete(TABLE_TRIP, COLUMN_TRIP_ID + " = " + tripId, null);
    }

    public List<Trip> getAllTrips(){
        List<Trip> trips = new ArrayList<Trip>();

        Cursor cursor = database.query(
                TABLE_TRIP,
                COLUMNS_TRIP,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Trip trip = cursorToTrip(cursor);
            trips.add(trip);
            cursor.moveToNext();
        }

        cursor.close();
        return trips;
    }

    public List<LocationInfo> getTripAllLocations(long tripId) {
        List<LocationInfo> locations = new ArrayList();

        Cursor cursor = database.query(
                TABLE_LOCATION,
                COLUMNS_LOCATION,
                COLUMN_LOCATION_TRIPID + " = " + tripId,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            locations.add(cursorToLocationInfo(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return locations;
    }

    private Trip cursorToTrip(Cursor cursor){
        Trip trip = new Trip();
        int valueIndex = 0;
        trip.setId(cursor.getLong(valueIndex++));
        trip.setStartTime(cursor.getString(valueIndex++));
        trip.setEndTime(cursor.getString(valueIndex++));
        trip.setDuration(cursor.getLong(valueIndex++));
        trip.setStartMileage(cursor.getLong(valueIndex++));
        trip.setEndMileage(cursor.getLong(valueIndex++));
        trip.setFuelConsume(cursor.getLong(valueIndex++));
        trip.setTotalWarning(cursor.getLong(valueIndex++));
        trip.setRating(cursor.getLong(valueIndex++));
        return trip;
    }

    private LocationInfo cursorToLocationInfo(Cursor cursor) {
        LocationInfo locInfo = new LocationInfo();
        int valueIndex = 0;
        locInfo.setLocationId(cursor.getLong(valueIndex++));
        locInfo.setTripId(cursor.getLong(valueIndex++));
        locInfo.setLatitude(cursor.getDouble(valueIndex++));
        locInfo.setLongitude(cursor.getDouble(valueIndex++));
        locInfo.setAltitude(cursor.getDouble(valueIndex++));
        return locInfo;
    }
}
