package com.aronssondev.andreas.ecodriver;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import SQLiteDatabase.DriveRecord;

/**
 * Created by jied on 21/04/15.
 */
public class DriveDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_details);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            DriveRecord driveRecord = extras.getParcelable("selectedRecord");

            TextView tripTitle = (TextView) findViewById(R.id.textViewTripTitle);
            tripTitle.setText("Trip of " + driveRecord.getStartPlace() + " - " + driveRecord.getDestination());

            TextView tripTime = (TextView) findViewById(R.id.textViewTT);
            tripTime.setText(driveRecord.getDriveRecord());

            TextView duration = (TextView) findViewById(R.id.textViewDur);
            duration.setText(driveRecord.getTimeDuration());

            TextView distance = (TextView) findViewById(R.id.textViewDis);
            distance.setText(String.valueOf(driveRecord.getDistance()));

            TextView avgSpeed = (TextView) findViewById(R.id.textViewAS);
            avgSpeed.setText(String.valueOf(driveRecord.getAvgSpeed()));

            TextView avgRPM = (TextView) findViewById(R.id.textViewAR);
            avgRPM.setText(String.valueOf(driveRecord.getAvgRPM()));

            TextView fuel = (TextView) findViewById(R.id.textViewF);
            fuel.setText(String.valueOf(driveRecord.getFuelConsume()));

            TextView emission = (TextView) findViewById(R.id.textViewE);
            emission.setText(String.valueOf(driveRecord.getEmissionCO2()));
        }

        TextView tripLog = (TextView) findViewById(R.id.textViewTL);
        tripLog.setText("At latitude/longitude, you have high RPM of ... and Consumed " +
                "too much fuel of .... You could improve it by doing ... in future driving.");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
