package com.gu.gminions.ecodriver;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gu.gminions.db.Trip;

import java.util.Random;


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
            Trip trip = extras.getParcelable("selectedTrip");

            // TODO: proper fix
            String[] places = {"Stockholm", "Göteborg", "Malmö", "Borås", "Varberg", "Karlstad" ,"Helsingborg"};
            Random rand = new Random();
            String startPlace = places[rand.nextInt(7)];
            String destination = places[rand.nextInt(7)];

            TextView tripTitle = (TextView) findViewById(R.id.textViewTripTitle);
            tripTitle.setText("Trip of " + startPlace + " - " + destination);
            // TODO: proper fix

            TextView tripTime = (TextView) findViewById(R.id.textViewTT);
            tripTime.setText(trip.getStartTime());

            TextView duration = (TextView) findViewById(R.id.textViewDur);
            long tripDuration = trip.getDuration() / 1000;
            long sec = tripDuration % 60;
            long min = (tripDuration / 60) % 60;
            long hour = tripDuration / 3600;
            duration.setText(String.format("%02d:%02d:%02d", hour, min, sec));

            TextView distance = (TextView) findViewById(R.id.textViewDis);
            float tripDist  = (trip.getEndMileage() - trip.getStartMileage()) / 1000.f;
            distance.setText(String.valueOf(tripDist));

            TextView avgSpeed = (TextView) findViewById(R.id.textViewAS);
            avgSpeed.setText(String.valueOf(tripDist/tripDuration));

            TextView avgRPM = (TextView) findViewById(R.id.textViewAR);
            avgRPM.setText(String.valueOf(0)); // trip.getAvgRPM())); // TODO: proper fix

            TextView fuel = (TextView) findViewById(R.id.textViewF);
            fuel.setText(String.valueOf(trip.getFuelConsume()));

            TextView emission = (TextView) findViewById(R.id.textViewE);
            emission.setText(String.valueOf(0)); // trip.getEmissionCO2())); // TODO: proper fix
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
