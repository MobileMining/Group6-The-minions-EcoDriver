package SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jied on 01/04/15.
 * DAO, database access object;
 * maintain database connection, supporting add new and fetch all data.
 */
public class DataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_STARTTIME,
            MySQLiteHelper.COLUMN_ENDTIME,
            MySQLiteHelper.COLUMN_DURATION,
            MySQLiteHelper.COLUMN_STARTMILEAGE,
            MySQLiteHelper.COLUMN_ENDMILEAGE,
            MySQLiteHelper.COLUMN_DISTANCE,
            MySQLiteHelper.COLUMN_STARTPLACE,
            MySQLiteHelper.COLUMN_DESTINATION,
            MySQLiteHelper.COLUMN_AVGSPEED,
            MySQLiteHelper.COLUMN_AVGRPM,
            MySQLiteHelper.COLUMN_FUEL,
            MySQLiteHelper.COLUMN_EMISSION,
            MySQLiteHelper.COLUMN_RATING
    };

    public DataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Trip createTrip(
            String startTime,
            String endTime,
            String timeDuration,
            long startMileage,
            long endMileage,
            long distance,
            String startPlace,
            String destination,
            long avgSpeed,
            long avgRPM,
            long fuelConsume,
            long emission,
            long rating){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_STARTTIME, startTime);
        values.put(MySQLiteHelper.COLUMN_ENDTIME, endTime);
        values.put(MySQLiteHelper.COLUMN_DURATION, timeDuration);
        values.put(MySQLiteHelper.COLUMN_STARTMILEAGE, startMileage);
        values.put(MySQLiteHelper.COLUMN_ENDMILEAGE, endMileage);
        values.put(MySQLiteHelper.COLUMN_DISTANCE, distance);
        values.put(MySQLiteHelper.COLUMN_STARTPLACE, startPlace);
        values.put(MySQLiteHelper.COLUMN_DESTINATION, destination);
        values.put(MySQLiteHelper.COLUMN_AVGSPEED, avgSpeed);
        values.put(MySQLiteHelper.COLUMN_AVGRPM, avgRPM);
        values.put(MySQLiteHelper.COLUMN_FUEL, fuelConsume);
        values.put(MySQLiteHelper.COLUMN_EMISSION, emission);
        values.put(MySQLiteHelper.COLUMN_RATING, rating);

        //insert row
        long insertId = database.insert(MySQLiteHelper.TABLE_TRIPS, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRIPS, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Trip newTrip = cursorToTrip(cursor);
        cursor.close();

        return newTrip;
    }

    public void deleteTrip(Trip trip){
        long id = trip.getId();
        System.out.println("Trip deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_TRIPS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Trip> getAllTrips(){
        List<Trip> trips = new ArrayList<Trip>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRIPS,
                allColumns, null, null, null, null, null);

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
        trip.setId(cursor.getLong(0));
        trip.setStartTime(cursor.getString(1));
        trip.setEndTime(cursor.getString(2));
        trip.setTimeDuration(cursor.getString(3));
        trip.setStartMileage(cursor.getLong(4));
        trip.setEndMileage(cursor.getLong(5));
        trip.setDistance(cursor.getLong(6));
        trip.setStartPlace(cursor.getString(7));
        trip.setDestination(cursor.getString(8));
        trip.setAvgSpeed(cursor.getLong(9));
        trip.setAvgRPM(cursor.getLong(10));
        trip.setFuelConsume(cursor.getLong(11));
        trip.setEmissionCO2(cursor.getLong(12));
        trip.setRating(cursor.getLong(13));
        return trip;
    }
}
