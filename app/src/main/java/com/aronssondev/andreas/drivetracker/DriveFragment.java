package com.aronssondev.andreas.drivetracker;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DriveFragment extends Fragment {
    private static final String TAG = "DriveFragment";
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_RUN = 0;
    private static final int LOAD_LOCATION = 1;
    
    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if (!mDriveManager.isTrackingDrive(mDrive))
                return;
            mLastLocation = loc;
            if (isVisible()) 
                updateUI();
        }
        
        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
        
    };
    
    private DriveManager mDriveManager;
    
    private Drive mDrive;
    private Location mLastLocation;

    private Button mStartButton, mStopButton, mMapButton;
    private TextView mStartedTextView, mLatitudeTextView, 
        mLongitudeTextView, mAltitudeTextView, mDurationTextView;
    
    public static DriveFragment newInstance(long driveId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, driveId);
        DriveFragment rf = new DriveFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDriveManager = DriveManager.get(getActivity());

        // check for a Drive ID as an argument, and find the drive
        Bundle args = getArguments();
        if (args != null) {
            long driveId = args.getLong(ARG_RUN_ID, -1);
            if (driveId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new DriveLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drive, container, false);
        
        mStartedTextView = (TextView)view.findViewById(R.id.drive_startedTextView);
        mLatitudeTextView = (TextView)view.findViewById(R.id.drive_latitudeTextView);
        mLongitudeTextView = (TextView)view.findViewById(R.id.drive_longitudeTextView);
        mAltitudeTextView = (TextView)view.findViewById(R.id.drive_altitudeTextView);
        mDurationTextView = (TextView)view.findViewById(R.id.drive_durationTextView);
        
        mStartButton = (Button)view.findViewById(R.id.drive_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrive == null) {
                    mDrive = mDriveManager.startNewDrive();
                } else {
                    mDriveManager.startTrackingDrive(mDrive);
                }
                updateUI();
            }
        });
        
        mStopButton = (Button)view.findViewById(R.id.drive_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDriveManager.stopDrive();
                updateUI();
            }
        });

        mMapButton = (Button)view.findViewById(R.id.drive_mapButton);
        mMapButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent i = new Intent(getActivity(), DriveMapActivity.class);
                                              i.putExtra(DriveMapActivity.EXTRA_RUN_ID, mDrive.getId());
                                              startActivity(i);
                                          }
                                      });

                updateUI();
        
        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, 
                new IntentFilter(DriveManager.ACTION_LOCATION));
    }
    
    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }
    
    private void updateUI() {
        boolean started = mDriveManager.isTrackingDrive();
        boolean trackingThisDrive = mDriveManager.isTrackingDrive(mDrive);
        
        if (mDrive != null)
            mStartedTextView.setText(mDrive.getStartDate().toString());
        
        int durationSeconds = 0;
        if (mDrive != null && mLastLocation != null) {
            durationSeconds = mDrive.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));

            mMapButton.setEnabled(true);
        } else {
            mMapButton.setEnabled(false);
        }
        mDurationTextView.setText(Drive.formatDuration(durationSeconds));
        
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started && trackingThisDrive);
    }
    
    private class DriveLoaderCallbacks implements LoaderCallbacks<Drive> {
        
        @Override
        public Loader<Drive> onCreateLoader(int id, Bundle args) {
            return new DriveLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Drive> loader, Drive drive) {
            mDrive = drive;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Drive> loader) {
            // do nothing
        }
    }

    private class LocationLoaderCallbacks implements LoaderCallbacks<Location> {
        
        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location location) {
            mLastLocation = location;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {
            // do nothing
        }
    }
}
