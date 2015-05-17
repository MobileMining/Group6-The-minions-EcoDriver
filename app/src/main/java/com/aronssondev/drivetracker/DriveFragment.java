package com.aronssondev.drivetracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSFloat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aronssondev.drivetracker.db.DriveDataSource;
import com.aronssondev.drivetracker.db.Trip;
import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveListener;
import com.swedspot.automotiveapi.AutomotiveManager;
import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;
import com.swedspot.vil.distraction.LightMode;
import com.swedspot.vil.distraction.StealthMode;
import com.swedspot.vil.policy.AutomotiveCertificate;


import java.text.DateFormat;
import java.util.Date;

import static android.os.SystemClock.elapsedRealtime;

public class DriveFragment extends Fragment {

    public static final String EXTRA_DRIVE_ID = "com.aronssondev.drivetracker.drive_id";

    private static final int LOAD_DRIVE = 1;
    private static final int LOAD_LOCATION = 2;

    private Button mStartButton, mStopButton, mMapButton;
    private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView,
            mAltitudeTextView, mDurationTextView;

    private DriveManager mDriveManager;

    private Drive mDrive;

    private Location mLastLocation;


    //AGA

    private Handler handler;
    private float rpm;
    private MediaPlayer mediaPlayer;
    private float lastWarningMilli = elapsedRealtime();
    private boolean isTracking;
    private final int trackingStartBGColor = android.R.color.holo_green_dark;
    private final int trackingStopBGColor = android.R.color.holo_red_dark;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mDriveManager = DriveManager.getInstance(getActivity());

        long driveId = getActivity().getIntent().getLongExtra(EXTRA_DRIVE_ID, 0);

        if (driveId != 0) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOAD_DRIVE, null, mDriveLoaderCallbacks);
            loaderManager.initLoader(LOAD_LOCATION, null, mLocationLoaderCallbacks);
        }


    //AGA

//        ((ProgressBar)findViewById(R.id.pb_speed)).setProgress(0);
//        ((ProgressBar)findViewById(R.id.pb_rpm)).setProgress(0);
//        ((ProgressBar)findViewById(R.id.pb_fuel)).setProgress(0);
    }



    AutomotiveManager amApi;

    public void startTracking(final ProgressBar pbSpeed, final ProgressBar pbRpm, final ProgressBar pbFuel, final TextView tvSpeed, final TextView tvRpm, final TextView tvFuel){
//        final TextView tvSpeed = (TextView)findViewById(R.id.tv_speed);
//        final TextView tvRpm = (TextView)findViewById(R.id.tv_rpm);
//        final TextView tvFuel = (TextView)findViewById(R.id.tv_fuel);
//
//        final ProgressBar pbSpeed = (ProgressBar)findViewById(R.id.pb_speed);
//        final ProgressBar pbRpm = (ProgressBar)findViewById(R.id.pb_rpm);
//        final ProgressBar pbFuel = (ProgressBar)findViewById(R.id.pb_fuel);

        pbSpeed.setProgress(0);
        pbRpm.setProgress(0);
        pbFuel.setProgress(0);

        final float MaxSpeed = 300.f;
        final float MaxRpm = 10000.f;

        handler = new Handler();
        handler.postDelayed(runnable, 100);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... vs) {
                amApi = AutomotiveFactory.createAutomotiveManagerInstance(
                        new AutomotiveCertificate(new byte[0]),
                        new AutomotiveListener() { // Listener that observes the Signals
                            @Override
                            public void receive(final AutomotiveSignal automotiveSignal) {
                                if (automotiveSignal.getSignalId() == AutomotiveSignalId.FMS_WHEEL_BASED_SPEED) {

                                    final float speed = ((SCSFloat) automotiveSignal.getData()).getFloatValue();

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
                                /*
                                ds.post(new Runnable() { // Post the result back to the View/UI thread
                                    public void run() {
                                        //ds.setTextSize(driverDistractionLevel.getLevel() * 10.0F + 12.0F);
                                    }
                                });
                                */
                            }

                            @Override
                            public void lightModeChanged(LightMode lightMode) {

                            }

                            @Override
                            public void stealthModeChanged(StealthMode stealthMode) {

                            }
                        }
                );
                amApi.register(
                        AutomotiveSignalId.FMS_WHEEL_BASED_SPEED,   // Register for the speed signal
                        AutomotiveSignalId.FMS_ENGINE_SPEED,        // RPM signal
                        AutomotiveSignalId.FMS_FUEL_LEVEL_1);       // fuel signal
                return null;
            }
        }.execute();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
           if (rpm > 3000 && lastWarningMilli + 15000 < elapsedRealtime()) {
                    mediaPlayer.start();
                Toast.makeText(getActivity(), "RPM too high!", Toast.LENGTH_SHORT).show();
               lastWarningMilli = elapsedRealtime();
            }
            handler.postDelayed(runnable, 100);
        }
    };


    //END of AGA

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drive, container, false);

        mStartedTextView = (TextView) v.findViewById(R.id.drive_startedTextView);
        mLatitudeTextView = (TextView) v.findViewById(R.id.drive_latitudeTextView);
        mLongitudeTextView = (TextView) v.findViewById(R.id.drive_longitudeTextView);
        mAltitudeTextView = (TextView) v.findViewById(R.id.drive_altitudeTextView);
        mDurationTextView = (TextView) v.findViewById(R.id.drive_durationTextView);


        //aga
        final TextView tvSpeed = (TextView)v.findViewById(R.id.tv_speed);
        final TextView tvRpm = (TextView)v.findViewById(R.id.tv_rpm);
        final TextView tvFuel = (TextView)v.findViewById(R.id.tv_fuel);

        final ProgressBar pbSpeed = (ProgressBar)v.findViewById(R.id.pb_speed);
        final ProgressBar pbRpm = (ProgressBar)v.findViewById(R.id.pb_rpm);
        final ProgressBar pbFuel = (ProgressBar)v.findViewById(R.id.pb_fuel);

        ((ProgressBar)v.findViewById(R.id.pb_speed)).setProgress(0);
        ((ProgressBar)v.findViewById(R.id.pb_rpm)).setProgress(0);
        ((ProgressBar)v.findViewById(R.id.pb_fuel)).setProgress(0);

        //end of AGA

        isTracking = false;



        final DriveDataSource dataSource = new DriveDataSource(getActivity());
        dataSource.open();



        final Button btnStartStop = (Button) v.findViewById(R.id.buttonStartStop);
        btnStartStop.setText("Start");
        btnStartStop.setBackgroundColor(getResources().getColor(trackingStartBGColor));
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracking) {
                    isTracking = true;
                    btnStartStop.setText("Stop");
                    btnStartStop.setBackgroundColor(getResources().getColor(trackingStopBGColor));
                    startTracking(pbSpeed, pbRpm, pbFuel, tvSpeed, tvRpm, tvFuel);
                    Toast.makeText(getActivity(), "New trip gets started!", Toast.LENGTH_SHORT).show();
                } else {
                    isTracking = false;
                    // add to database
                    DateFormat df = DateFormat.getDateTimeInstance();
                    String startTime = df.format(new Date());

                    Trip trip = new Trip();
                    trip.setStartTime(startTime);
                   // dataSource.createTrip(trip);

                    // Stop RPM warning timer
                    handler.removeCallbacks(runnable);

                    // button turns to start
                    btnStartStop.setText("Start");
                    btnStartStop.setBackgroundColor(getResources().getColor(trackingStartBGColor));
                    // Toast.makeText(getApplicationContext(), "Trip ended and records saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mStartButton = (Button) v.findViewById(R.id.drive_StartButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrive = mDriveManager.startTrackingDrive(mDrive);

                updateUI();
            }
        });

        mStopButton = (Button) v.findViewById(R.id.drive_StopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDriveManager.stopTrackingDrive();

                updateUI();
            }
        });

        mMapButton = (Button) v.findViewById(R.id.drive_mapButton);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DriveMapActivity.class);
                i.putExtra(EXTRA_DRIVE_ID, mDrive.getId());
                startActivity(i);
            }
        });

        updateUI();

        return v;

        // setContentView(R.layout.activity_track_driving);

        // mediaPlayer = MediaPlayer.create(this, R.raw.beep);




    }

    private void updateUI() {
        boolean started = mDriveManager.isTrackingDrive();
        boolean trackingThisDrive = mDriveManager.isTrackingDrive(mDrive);
        boolean hasLastLocation = (mDrive != null) && (mLastLocation != null);

        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(trackingThisDrive);
        mMapButton.setEnabled(hasLastLocation);

        if (mDrive != null) {
            mStartedTextView.setText(mDrive.getFormattedDate());
        }

        int durationSeconds = 0;

        if (mDrive != null && mLastLocation != null) {
            durationSeconds = mDrive.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
        }

        mDurationTextView.setText(Drive.formatDuration(durationSeconds));
    }

    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location location) {
            // super.onLocationReceived(context, location);

            if (!mDriveManager.isTrackingDrive(mDrive)) {
                return;
            }

            mLastLocation = location;
            if (isVisible()) {
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            // super.onProviderEnabledChanged(enabled);

            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver,
                new IntentFilter(DriveManager.ACTION_LOCATION));

        long driveId = getActivity().getIntent().getLongExtra(EXTRA_DRIVE_ID, 0);

        if (driveId != 0) {
            mDrive = mDriveManager.getDrive(driveId);
            mLastLocation = mDriveManager.getLastLocationForDrive(mDrive.getId());
        }
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    private static class DriveLoader extends DataLoader<Drive> {

        private long mDriveId;

        public DriveLoader(Context context, long driveId) {
            super(context);
            mDriveId = driveId;
        }

        @Override
        public Drive loadInBackground() {
            return DriveManager.getInstance(getContext())
                    .getDrive(mDriveId);
        }
    }

    private LoaderManager.LoaderCallbacks<Drive> mDriveLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Drive>() {
                @Override
                public Loader<Drive> onCreateLoader(int id, Bundle args) {
                    return new DriveLoader(getActivity(),
                            getActivity().getIntent().getLongExtra(EXTRA_DRIVE_ID, 0));
                }

                @Override
                public void onLoadFinished(Loader<Drive> loader, Drive data) {
                    mDrive = data;
                    updateUI();
                }

                @Override
                public void onLoaderReset(Loader<Drive> loader) {

                }
            };

    private static class LastLocationLoader extends DataLoader<Location> {

        private long mDriveId;

        public LastLocationLoader(Context context, long driveId) {
            super(context);
            mDriveId = driveId;
        }

        @Override
        public Location loadInBackground() {
            return DriveManager.getInstance(getContext())
                    .getLastLocationForDrive(mDriveId);
        }
    }

    private LoaderManager.LoaderCallbacks<Location> mLocationLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Location>() {
                @Override
                public Loader<Location> onCreateLoader(int id, Bundle args) {
                    return new LastLocationLoader(getActivity(),
                            getActivity().getIntent().getLongExtra(EXTRA_DRIVE_ID, 0));
                }

                @Override
                public void onLoadFinished(Loader<Location> loader, Location data) {
                    mLastLocation = data;
                    updateUI();
                }

                @Override
                public void onLoaderReset(Loader<Location> loader) {

                }
            };
}
