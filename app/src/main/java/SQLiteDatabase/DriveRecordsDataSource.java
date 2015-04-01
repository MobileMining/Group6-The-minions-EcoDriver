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
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_RECORD};

    public DriveRecordsDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public DriveRecord createRecord(String record){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_RECORD, record);
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
        return record;
    }
}
