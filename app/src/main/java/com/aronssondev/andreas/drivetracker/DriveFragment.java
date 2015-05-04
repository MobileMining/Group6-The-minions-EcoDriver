package com.aronssondev.andreas.drivetracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DriveFragment extends Fragment {

    public static final String EXTRA_RUN_ID = "com.aronssondev.andreas.drivetracker.drive_id";

    private static final int LOAD_RUN = 1;
    private static final int LOAD_LOCATION = 2;

    private Button mStartButton, mStopButton, mMapButton;
    private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView,
            mAltitudeTextView, mDurationTextView;

    private DriveManager mDriveManager;

    private Drive mDrive;

    private Location mLastLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mDriveManager = DriveManager.getInstance(getActivity());

        long driveId = getActivity().getIntent().getLongExtra(EXTRA_RUN_ID, 0);

        if (driveId != 0) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOAD_RUN, null, mDriveLoaderCallbacks);
            loaderManager.initLoader(LOAD_LOCATION, null, mLocationLoaderCallbacks);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drive, container, false);

        mStartedTextView = (TextView) v.findViewById(R.id.drive_startedTextView);
        mLatitudeTextView = (TextView) v.findViewById(R.id.drive_latitudeTextView);
        mLongitudeTextView = (TextView) v.findViewById(R.id.drive_longitudeTextView);
        mAltitudeTextView = (TextView) v.findViewById(R.id.drive_altitudeTextView);
        mDurationTextView = (TextView) v.findViewById(R.id.drive_durationTextView);

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
                i.putExtra(EXTRA_RUN_ID, mDrive.getId());
                startActivity(i);
            }
        });

        updateUI();

        return v;
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

        long driveId = getActivity().getIntent().getLongExtra(EXTRA_RUN_ID, 0);

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
                            getActivity().getIntent().getLongExtra(EXTRA_RUN_ID, 0));
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
                            getActivity().getIntent().getLongExtra(EXTRA_RUN_ID, 0));
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
