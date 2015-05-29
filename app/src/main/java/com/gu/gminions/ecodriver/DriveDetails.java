package com.gu.gminions.ecodriver;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gu.gminions.db.DriveDataSource;
import com.gu.gminions.db.LocationInfo;
import com.gu.gminions.db.Trip;
import com.gu.gminions.db.Warning;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jied on 21/04/15.
 */
public class DriveDetails extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<LocationInfo>> {
    DriveDataSource dataSource;
    Trip trip;
    List<Warning> warnings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_details);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            trip = extras.getParcelable("selectedTrip");

            TextView tripTitle = (TextView) findViewById(R.id.textViewTripTitle);

            TextView tripTime = (TextView) findViewById(R.id.textViewTT);
            tripTime.setText(trip.getStartTime());

            TextView duration = (TextView) findViewById(R.id.textViewDur);
            long tripDuration = trip.getDuration() / 1000;
            long sec = tripDuration % 60;
            long min = (tripDuration / 60) % 60;
            long hour = tripDuration / 3600;
            duration.setText(String.format("%02d:%02d:%02d", hour, min, sec));

            TextView distance = (TextView) findViewById(R.id.textViewDis);
            long tripDist  = (trip.getEndMileage() - trip.getStartMileage()) / 1000 * 3600;
            distance.setText(String.valueOf(tripDist) + " km");

            TextView avgSpeed = (TextView) findViewById(R.id.textViewAS);
            avgSpeed.setText(String.valueOf(tripDist/tripDuration) + " km/h");

            TextView fuel = (TextView) findViewById(R.id.textViewF);
            fuel.setText(String.valueOf(trip.getFuelConsume()) + "%");
        }

        TextView tripLog = (TextView) findViewById(R.id.textViewTL);

        // start loading warning & locations
        if(trip != null) {
            dataSource = new DriveDataSource(this);
            dataSource.open();
            warnings = new ArrayList();
            warnings = dataSource.getTripAllWarnings(trip.getId());
            getSupportLoaderManager().initLoader(-1, null, this);
        }

        /*
        Warning A-D
        A: RPM between 2000-2500
        B: RPM higher than 2500
        C: Hard acceleration
        D: Hard braking
        */
        int warningCountA = 0, warningCountB = 0, warningCountC = 0, warningCountD = 0;
        if (warnings != null) {
            for (Warning wn : warnings) {
                if (wn.getType() == 1) {
                    warningCountA++;
                }
                else if (wn.getType() == 2) {
                    warningCountB++;
                }
                else if (wn.getType() == 3) {
                    warningCountC++;
                }
                else if (wn.getType() == 4) {
                    warningCountD++;
                }
            }
        }

        tripLog.setText("During the trip, you have received a total of " + trip.getTotalWarning() + " warnings.");
        if (warningCountB != 0){
            tripLog.setText(tripLog.getText() + " You've received " + warningCountB + " warnings for driving at very high RPM.");
        }
        if (warningCountC != 0){
            tripLog.setText(tripLog.getText() + " You've hard accelerated " + warningCountA + " times. Hard accelerating consumes more fuel than necessary!");
        }
        if (warningCountD != 0){
            tripLog.setText(tripLog.getText() + " You've hard braked " + warningCountA + " times. Try to plan ahead and slow down in time!");
        }
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

    @Override
    public Loader<List<LocationInfo>> onCreateLoader(int loaderID, Bundle bundle){
        return new AsyncTaskLoader<List<LocationInfo>>(this) {
            boolean needReload = true;

            @Override
            public List<LocationInfo> loadInBackground() {
                needReload = false;
                return dataSource.getTripAllLocations(trip.getId());
            }

            @Override
            public void deliverResult(List<LocationInfo> locations) {
                super.deliverResult(locations);
            }

            @Override
            protected void onStartLoading() {
                if(needReload)
                    forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<LocationInfo>> loader, final List<LocationInfo> locations) {
        if(locations.size() > 0) {
            final List<LocationInfo> localLocations = locations;

            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    double sumLat = 0;
                    double sumLng = 0;

                    // add polyline for all locations
                    PolylineOptions po = new PolylineOptions().geodesic(true);
                    for (LocationInfo li : localLocations) {
                        double lat = li.getLatitude();
                        double lng = li.getLongitude();

                        sumLat += lat;
                        sumLng += lng;

                        po = po.add(new LatLng(lat, lng));
                    }
                    googleMap.addPolyline(po);

                    // move camera to center of polyline
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sumLat / localLocations.size(), sumLng / localLocations.size()), 10));

                    // add markers for start/end of the trip
                    googleMap.addMarker(new MarkerOptions()
                            .title("")
                            .snippet("")
                            .position(new LatLng(localLocations.get(0).getLatitude(), localLocations.get(0).getLongitude())));

                    if(localLocations.size() > 1)
                        googleMap.addMarker(new MarkerOptions()
                                .title("")
                                .snippet("")
                                .position(new LatLng(localLocations.get(localLocations.size()-1).getLatitude(), localLocations.get(localLocations.size()-1).getLongitude())));

                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<List<LocationInfo>> loader) {
    }
}
