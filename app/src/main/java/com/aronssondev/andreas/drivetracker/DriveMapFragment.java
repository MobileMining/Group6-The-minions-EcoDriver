package com.aronssondev.andreas.drivetracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DriveMapFragment extends SupportMapFragment {

    private static final int LOAD_LOCATIONS = 1;

    private GoogleMap mGoogleMap;
    private DriveDatabaseHelper.LocationCursor mLocationCursor;
    private DriveManager mDriveManager;
    private Drive mDrive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        long driveId = getActivity().getIntent().getLongExtra(DriveFragment.EXTRA_RUN_ID, 0);

        if (driveId != 0) {
            getLoaderManager().initLoader(LOAD_LOCATIONS, null, mLoaderCallbacks);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mGoogleMap = getMap();
        mGoogleMap.setMyLocationEnabled(true);

        return v;
    }

    private static class LocationListCursorLoader extends SQLiteCursorLoader {

        private long mDriveId;

        public LocationListCursorLoader(Context context, long driveId) {
            super(context);
            mDriveId = driveId;
        }

        @Override
        protected Cursor loadCursor() {
            return DriveManager.getInstance(getContext()).queryLocationsForDrive(mDriveId);
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new LocationListCursorLoader(getActivity(),
                    getActivity().getIntent().getLongExtra(DriveFragment.EXTRA_RUN_ID, 0));
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mLocationCursor = (DriveDatabaseHelper.LocationCursor) data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mLocationCursor.close();
            mLocationCursor = null;
        }
    };

    private void updateUI() {
        if (mGoogleMap == null || mLocationCursor == null) {
            return;
        }

        PolylineOptions line = new PolylineOptions();

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        mLocationCursor.moveToFirst();

        while (!mLocationCursor.isAfterLast()) {
            Location location = mLocationCursor.getLocation();
            LatLng latLng = new LatLng(location.getLatitude(),
                    location.getLongitude());

            Resources resources = getResources();

            if (mLocationCursor.isFirst()) {
                String startDate = getFormattedDate(new Date(location.getTime()));

                MarkerOptions startMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(resources.getString(R.string.drive_start))
                        .snippet(resources.getString(
                                R.string.drive_started_at_format, startDate));

                mGoogleMap.addMarker(startMarkerOptions);
            } else if (mLocationCursor.isLast()) {
                String finishDate = getFormattedDate(new Date(location.getTime()));

                MarkerOptions finishMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(resources.getString(R.string.drive_finish))
                        .snippet(resources.getString(
                                R.string.drive_finished_at_format, finishDate));

                mGoogleMap.addMarker(finishMarkerOptions);
            }

            line.add(latLng);
            latLngBuilder.include(latLng);

            mLocationCursor.moveToNext();
        }

        mGoogleMap.addPolyline(line);

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        LatLngBounds latLngBounds = latLngBuilder.build();

        CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBounds,
                display.getWidth(), display.getHeight(), 15);

        mGoogleMap.moveCamera(movement);
    }

    private String getFormattedDate(Date date) {
        String format = "EEEE, MMM d, yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(date);
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

    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location location) {

            mDriveManager = DriveManager.getInstance(getActivity());

            long driveId = getActivity().getIntent().getLongExtra(DriveFragment.EXTRA_RUN_ID, 0);

            mDrive = mDriveManager.getDrive(driveId);

            if (!mDriveManager.isTrackingDrive(mDrive)) {
                return;
            }

            if (isVisible()) {
                mGoogleMap.clear();
                getLoaderManager().restartLoader(LOAD_LOCATIONS, null, mLoaderCallbacks);
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };
}
