package com.gu.gminions.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jied on 01/04/15.
 * DB connnector.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eco_drive.db";
    private static final int DATABASE_VERSION = 1;

    //TABLE_TRIPS
    public static final String TABLE_TRIPS = "trips";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STARTTIME = "startTime";
    public static final String COLUMN_ENDTIME = "endTime";
    public static final String COLUMN_DURATION = "timeDuration";
    public static final String COLUMN_STARTMILEAGE = "startMileage";
    public static final String COLUMN_ENDMILEAGE = "endMileage";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_STARTPLACE = "startPlace";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_AVGSPEED = "avgSpeed";
    public static final String COLUMN_AVGRPM = "avgRPM";
    public static final String COLUMN_FUEL = "fuelConsume";
    public static final String COLUMN_EMISSION = "emissionCO2";
    public static final String COLUMN_RATING = "rating";

    private static final String CREATE_TABLE_TRIPS = "create table " + TABLE_TRIPS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_STARTTIME + " text not null, "
            + COLUMN_ENDTIME + " text, "
            + COLUMN_DURATION + " text, "
            + COLUMN_STARTMILEAGE + " integer, "
            + COLUMN_ENDMILEAGE + " integer, "
            + COLUMN_DISTANCE + " integer, "
            + COLUMN_STARTPLACE + " text, "
            + COLUMN_DESTINATION + " text, "
            + COLUMN_AVGSPEED + " integer, "
            + COLUMN_AVGRPM + " integer, "
            + COLUMN_FUEL + " integer, "
            + COLUMN_EMISSION + " integer, "
            + COLUMN_RATING + " integer);";

/*    //TABLE_LOGS
    public static final String TABLE_LOGS = "logs";
    public static final String COLUMN_STARTTIME = "startTime";
    public static final String COLUMN_ENDTIME = "endTime";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_RPM = "RPM";
    public static final String COLUMN_FUEL = "fuelConsume";
    public static final String COLUMN_TYPE = "type";

    private static final String CREATE_TABLE_LOGS = "create table " + TABLE_LOGS + "("
            + COLUMN_STARTTIME + " text not null, "
            + COLUMN_ENDTIME + " text, "
            + COLUMN_LATITUDE + " integer, "
            + COLUMN_LONGITUDE + " integer, "
            + COLUMN_SPEED + " integer, "
            + COLUMN_RPM + " integer, "
            + COLUMN_FUEL + " integer, "
            + COLUMN_TYPE + " integer);";
*/
    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRIPS);
//        db.execSQL(CREATE_TABLE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }
}
