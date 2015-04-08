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
public class DriveRecordsDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_RECORD,
            MySQLiteHelper.COLUMN_STARTPLACE,
            MySQLiteHelper.COLUMN_DESTINATION,
            MySQLiteHelper.COLUMN_DISTANCE,
            MySQLiteHelper.COLUMN_DURATION,
            MySQLiteHelper.COLUMN_AVGSPEED,
            MySQLiteHelper.COLUMN_AVGRPM,
            MySQLiteHelper.COLUMN_FUEL,
            MySQLiteHelper.COLUMN_EMISSION
    };

    public DriveRecordsDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public DriveRecord createRecord(
            String record,
            String startPlace,
            String destination,
            long distance,
            String timeDuration,
            long avgSpeed,
            long avgRPM,
            long fuelConsume,
            long emission){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_RECORD, record);
        values.put(MySQLiteHelper.COLUMN_STARTPLACE, startPlace);
        values.put(MySQLiteHelper.COLUMN_DESTINATION, destination);
        values.put(MySQLiteHelper.COLUMN_DISTANCE, distance);
        values.put(MySQLiteHelper.COLUMN_DURATION, timeDuration);
        values.put(MySQLiteHelper.COLUMN_AVGSPEED, avgSpeed);
        values.put(MySQLiteHelper.COLUMN_AVGRPM, avgRPM);
        values.put(MySQLiteHelper.COLUMN_FUEL, fuelConsume);
        values.put(MySQLiteHelper.COLUMN_EMISSION, emission);
        long insertId = database.insert(MySQLiteHelper.TABLE_RECORDS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECORDS, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        DriveRecord newRecord = cursorToRecord(cursor);
        cursor.close();
        return newRecord;
    }

    public void deleteRecord(DriveRecord driveRecord){
        long id = driveRecord.getId();
        System.out.println("Record deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_RECORDS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<DriveRecord> getAllRecords(){
        List<DriveRecord> records = new ArrayList<DriveRecord>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECORDS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DriveRecord record = cursorToRecord(cursor);
            records.add(record);
            cursor.moveToNext();
        }

        cursor.close();
        return records;
    }

    private DriveRecord cursorToRecord(Cursor cursor){
        DriveRecord record = new DriveRecord();
        record.setId(cursor.getLong(0));
        record.setDriveRecord(cursor.getString(1));
        record.setStartPlace(cursor.getString(2));
        record.setDestination(cursor.getString(3));
        record.setDistance(cursor.getLong(4));
        record.setTimeDuration(cursor.getString(5));
        record.setAvgSpeed(cursor.getLong(6));
        record.setAvgRPM(cursor.getLong(7));
        record.setFuelConsume(cursor.getLong(8));
        record.setEmissionCO2(cursor.getLong(9));
        return record;
    }
}
