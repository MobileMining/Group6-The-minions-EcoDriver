package com.aronssondev.drivetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.aronssondev.drivetracker.DriveDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.aronssondev.drivetracker.db.DriveDatabaseConfig.*;

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

    public long createTrip(Trip trip) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_TRIP_STARTTIME, trip.getStartTime());
        values.put(COLUMN_TRIP_ENDTIME, trip.getEndTime());
        values.put(COLUMN_TRIP_STARTMILEAGE, trip.getStartMileage());
        values.put(COLUMN_TRIP_ENDMILEAGE, trip.getEndMileage());
        values.put(COLUMN_TRIP_FUEL, trip.getFuelConsume());
        values.put(COLUMN_TRIP_WARNING, trip.getTotalWarning());
        values.put(COLUMN_TRIP_RATING, trip.getRating());

        //insert row
        long id = database.insert(TABLE_TRIP, null, values);

        Cursor cursor = database.query(
                TABLE_TRIP,
                COLUMNS_TRIP,
                COLUMN_TRIP_ID + " = " + id,
                null,
                null,
                null,
                null);

        trip.setId(id);

        return id;
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

    private Trip cursorToTrip(Cursor cursor){
        Trip trip = new Trip();
        int valueIndex = 0;
        trip.setId(cursor.getLong(valueIndex++));
        trip.setStartTime(cursor.getString(valueIndex++));
        trip.setEndTime(cursor.getString(valueIndex++));
        trip.setStartMileage(cursor.getLong(valueIndex++));
        trip.setEndMileage(cursor.getLong(valueIndex++));
        trip.setFuelConsume(cursor.getLong(valueIndex++));
        trip.setTotalWarning(cursor.getLong(valueIndex++));
        trip.setRating(cursor.getLong(valueIndex++));
        return trip;
    }

}
