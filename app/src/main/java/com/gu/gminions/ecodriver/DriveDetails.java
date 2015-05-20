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

import java.util.List;


/**
 * Created by jied on 21/04/15.
 */
public class DriveDetails extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<LocationInfo>> {
    DriveDataSource dataSource;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_details);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            trip = extras.getParcelable("selectedTrip");

            // TODO: proper fix
            //String[] places = {"Stockholm", "Göteborg", "Malmö", "Borås", "Varberg", "Karlstad" ,"Helsingborg"};
            //Random rand = new Random();
            //String startPlace = places[rand.nextInt(7)];
            //String destination = places[rand.nextInt(7)];

            TextView tripTitle = (TextView) findViewById(R.id.textViewTripTitle);
            //tripTitle.setText("Trip of " + startPlace + " - " + destination);
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
            long tripDist  = (trip.getEndMileage() - trip.getStartMileage()) / 1000;
            distance.setText(String.valueOf(tripDist) + " km");  // TODO: *3600 for final code

            TextView avgSpeed = (TextView) findViewById(R.id.textViewAS);
            avgSpeed.setText(String.valueOf(tripDist/tripDuration) + " km/h");

            TextView avgRPM = (TextView) findViewById(R.id.textViewAR);
            avgRPM.setText(String.valueOf(0)); // trip.getAvgRPM())); // TODO: proper fix

            TextView fuel = (TextView) findViewById(R.id.textViewF);
            fuel.setText(String.valueOf(trip.getFuelConsume()) + "%");

            TextView emission = (TextView) findViewById(R.id.textViewE);
            emission.setText(String.valueOf(0)); // trip.getEmissionCO2())); // TODO: proper fix
        }

        TextView tripLog = (TextView) findViewById(R.id.textViewTL);
        tripLog.setText("At latitude/longitude, you have high RPM of ... and Consumed " +
                "too much fuel of .... You could improve it by doing ... in future driving.");

        // start loading locations
        if(trip != null) {
            dataSource = new DriveDataSource(this);
            dataSource.open();
            getSupportLoaderManager().initLoader(-1, null, this);
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
