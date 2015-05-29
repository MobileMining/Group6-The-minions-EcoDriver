package com.gu.gminions.ecodriver;

import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.SCSLong;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;

import com.gu.gminions.db.DriveDataSource;
import com.gu.gminions.db.Trip;
import com.gu.gminions.db.Warning;
import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveListener;
import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;
import com.swedspot.vil.distraction.LightMode;
import com.swedspot.vil.distraction.StealthMode;
import com.swedspot.vil.policy.AutomotiveCertificate;

import java.text.DateFormat;
import java.util.Date;
import java.util.ArrayList;

import static android.os.SystemClock.elapsedRealtime;


public class TrackDriving extends ActionBarActivity {

    private Handler handler;
    private float rpm, speed, lastSpeed;
    private boolean soundOn = true;
    private MediaPlayer mediaPlayer;
    private float lastWarningMilli = elapsedRealtime();

    private boolean isTracking;
    private final int trackingStartBGColor = android.R.color.holo_green_dark;
    private final int trackingStopBGColor = android.R.color.holo_red_dark;

    private DriveDataSource dataSource;

    private Date startTime;
    private long startMileage;
    private long endMileage;
    private float startFuel;
    private float endFuel;

    private ArrayList<Location> trackedLocations;
    private ArrayList<Warning> warnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_driving);

        // Load sound
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);

        dataSource = new DriveDataSource(this);
        dataSource.open();

        isTracking = false;

        warnings = new ArrayList<Warning>();

        final Button btnStartStop = (Button) findViewById(R.id.buttonStartStop);
        btnStartStop.setText("Start");
        btnStartStop.setBackgroundColor(getResources().getColor(trackingStartBGColor));
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracking) {
                    // update UI
                    btnStartStop.setText("Stop");
                    btnStartStop.setBackgroundColor(getResources().getColor(trackingStopBGColor));

                    // update tip
                    Toast.makeText(getApplicationContext(), "New trip gets started!", Toast.LENGTH_SHORT).show();

                    // start RPM warning timer
                    handler = new Handler();
                    handler.postDelayed(runnable, 100);

                    // get started with tracking
                    startTracking();

                    isTracking = true;
                } else {
                    // update UI
                    btnStartStop.setText("Start");
                    btnStartStop.setBackgroundColor(getResources().getColor(trackingStartBGColor));

                    // update tip
                    Toast.makeText(getApplicationContext(), "Trip ended and records saved!", Toast.LENGTH_SHORT).show();

                    // Stop RPM warning timer
                    handler.removeCallbacks(runnable);

                    // stop with tracking
                    stopTracking();

                    isTracking = false;
                }
            }
        });

        ((ProgressBar) findViewById(R.id.pb_speed)).setProgress(0);
        ((ProgressBar) findViewById(R.id.pb_rpm)).setProgress(0);
        ((ProgressBar) findViewById(R.id.pb_fuel)).setProgress(0);
    }

    private void startTrackingAutomotive() {
        final TextView tvSpeed = (TextView)findViewById(R.id.tv_speed);
        final TextView tvRpm = (TextView)findViewById(R.id.tv_rpm);
        final TextView tvFuel = (TextView)findViewById(R.id.tv_fuel);

        final ProgressBar pbSpeed = (ProgressBar)findViewById(R.id.pb_speed);
        final ProgressBar pbRpm = (ProgressBar)findViewById(R.id.pb_rpm);
        final ProgressBar pbFuel = (ProgressBar)findViewById(R.id.pb_fuel);

        pbSpeed.setProgress(0);
        pbRpm.setProgress(0);
        pbFuel.setProgress(0);

        final float MaxSpeed = 300.f;
        final float MaxRpm = 10000.f;

        startTime = new Date();
        startMileage = 0;
        endMileage = 0;
        startFuel = 0;
        endFuel = 0;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... vs) {
                AutomotiveFactory.createAutomotiveManagerInstance(
                        new AutomotiveCertificate(new byte[0]),
                        new AutomotiveListener() { // Listener that observes the Signals
                            @Override
                            public void receive(final AutomotiveSignal automotiveSignal) {
                                if (automotiveSignal.getSignalId() == AutomotiveSignalId.FMS_WHEEL_BASED_SPEED) {

                                    speed = ((SCSFloat) automotiveSignal.getData()).getFloatValue();

                                    tvSpeed.post(new Runnable() { // Post the result back to the View/UI thread
                                        public void run() {
                                            tvSpeed.setText(String.format("%.1f km/h", speed));
                                        }
                                    });

                                    pbSpeed.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pbSpeed.setProgress((int) (100.f * speed / MaxSpeed));
                                        }
                                    });
                                } else if (automotiveSignal.getSignalId() == AutomotiveSignalId.FMS_ENGINE_SPEED) {
                                    rpm = ((SCSFloat) automotiveSignal.getData()).getFloatValue();

                                    tvRpm.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvRpm.setText(String.format("%.0f rpm", rpm));
                                        }
                                    });

                                    pbRpm.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pbRpm.setProgress((int) (100.f * rpm / MaxRpm));
                                        }
                                    });
                                } else if (automotiveSignal.getSignalId() == AutomotiveSignalId.FMS_FUEL_LEVEL_1) {
                                    final float fuel = ((SCSFloat) automotiveSignal.getData()).getFloatValue();

                                    tvFuel.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvFuel.setText(String.format("%.1f %%", fuel));
                                        }
                                    });

                                    pbFuel.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pbFuel.setProgress((int) (fuel));
                                        }
                                    });

                                    if(startFuel <= 0)
                                        startFuel = fuel;

                                    endFuel = fuel;

                                } else if (automotiveSignal.getSignalId() == AutomotiveSignalId.FMS_HIGH_RESOLUTION_TOTAL_VEHICLE_DISTANCE) {
                                    final long distance = ((SCSLong) automotiveSignal.getData()).getLongValue();

                                    if(startMileage <= 0)
                                        startMileage = distance;

                                    endMileage = distance;
                                }
                            }

                            @Override
                            public void timeout(int i) {
                            }

                            @Override
                            public void notAllowed(int i) {
                            }
                        },

                        new DriverDistractionListener() {       // Observe driver distraction level
                            @Override
                            public void levelChanged(final DriverDistractionLevel driverDistractionLevel) {
                            }

                            @Override
                            public void lightModeChanged(LightMode lightMode) {
                            }

                            @Override
                            public void stealthModeChanged(StealthMode stealthMode) {
                            }
                        }
                ).register(
                        AutomotiveSignalId.FMS_WHEEL_BASED_SPEED,   // Register for the speed signal
                        AutomotiveSignalId.FMS_ENGINE_SPEED,        // RPM signal
                        AutomotiveSignalId.FMS_FUEL_LEVEL_1,       // fuel signal
                        AutomotiveSignalId.FMS_HIGH_RESOLUTION_TOTAL_VEHICLE_DISTANCE);  // distance signal

                return null;
            }
        }.execute();
    }

    private void startTrackingLocation() {
        trackedLocations = new ArrayList<Location>();

        final long updateInterval = 15000; // every 15 sec
        final float updateDistance = 100; // every 100 meters

        ((LocationManager) this.getSystemService(this.LOCATION_SERVICE)).requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                updateInterval,
                updateDistance,
                new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        trackedLocations.add(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }

    private void startTracking(){
        startTrackingAutomotive();
        startTrackingLocation();
    }

    private void stopTracking() {
        // add to database
        if(dataSource != null) {
            DateFormat df = DateFormat.getDateTimeInstance();

            Date endTime = new Date();

            Trip trip = new Trip();
            trip.setStartTime(df.format(startTime));
            trip.setEndTime(df.format(endTime));
            trip.setDuration(endTime.getTime() - startTime.getTime());
            trip.setStartMileage(startMileage);
            trip.setEndMileage(endMileage);
            trip.setFuelConsume((long)(startFuel - endFuel));
            trip.setTotalWarning(warnings.size());

            dataSource.createTrip(trip, warnings, trackedLocations);
            warnings = null;
            trackedLocations = null;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (lastWarningMilli + 15000 < elapsedRealtime()){
                if (rpm > 2000 && rpm < 2500) {
                    warningMessage("RPM is high!", 1);
                }
                else if (rpm > 2500) {
                    warningMessage("RPM is very high!", 2);
                }
                else if (speed > lastSpeed + 11.2){
                    warningMessage("Hard acceleration!", 3);
                }
                else if (speed < lastSpeed - 11.2){
                    warningMessage("Hard brake!", 4);
                }
            }

            lastSpeed = speed;
            handler.postDelayed(runnable, 1000);
        }
    };

    private void warningMessage(String msg, int type){
        // Play sound and display message to driver
        if (soundOn) {
            mediaPlayer.start();
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        // Log the warning into database
        DateFormat df = DateFormat.getDateTimeInstance();
        Date time = new Date();
        Location currentLocation = new Location(this.LOCATION_SERVICE);

        Warning warning = new Warning();
        warning.setTime(df.format(time));
        warning.setSpeed(speed);
        warning.setAltitude(currentLocation.getAltitude());
        warning.setLatitude(currentLocation.getLatitude());
        warning.setLongitude(currentLocation.getLongitude());
        warning.setType(type);
        warnings.add(warning);

        lastWarningMilli = elapsedRealtime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_driving, menu);
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
        else if (id == R.id.warning_sound) {
            soundOn = !soundOn;
        }

        return super.onOptionsItemSelected(item);
    }
}
