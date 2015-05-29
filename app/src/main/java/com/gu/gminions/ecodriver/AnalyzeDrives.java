package com.gu.gminions.ecodriver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.gu.gminions.db.DriveDataSource;
import com.gu.gminions.db.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AnalyzeDrives extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<Trip>> {

    Set<Trip> mSelectedTrips = new TreeSet<Trip>();

    //Inner class for customized adapter for list view.
    private class TripAdapter extends ArrayAdapter<Trip>{
        private int resource;
        private LayoutInflater inflater;
        private Context context;
        private final int goodRatingColor = android.R.color.holo_green_dark;
        private final int mediaRatingColor = android.R.color.holo_orange_dark;
        private final int badRatingColor = android.R.color.holo_red_dark;
        private int selectedPosition = -1;

        public TripAdapter(Context cxt, int resourceId, List<Trip> objects){
            super(cxt, resourceId, objects);
            resource = resourceId;
            inflater = LayoutInflater.from(cxt);
            context = cxt;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            convertView = (ViewGroup) inflater.inflate(resource, null);
            final Trip trip = getItem(position);

            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            if(mSelectedTrips.contains(trip))
                checkBox.setChecked(true);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                        if(isChecked)
                            mSelectedTrips.add(trip);
                        else
                            mSelectedTrips.remove(trip);
                    }
                });

            TextView startTime = (TextView) convertView.findViewById(R.id.timeRecord);
            startTime.setText("Drive at " + trip.getStartTime());

            TextView tvRating = (TextView) convertView.findViewById(R.id.tvRating);
            tvRating.setText("7");

            //change background color of Rating

            if (tvRating.getText() == "1")
            {
                tvRating.setBackgroundColor(Color.parseColor("#003850"));
            }
            if (tvRating.getText() == "2")
            {
                tvRating.setBackgroundColor(Color.parseColor("#005852"));
            }
            if (tvRating.getText() == "3")
            {
                tvRating.setBackgroundColor(Color.parseColor("#4b8813"));
            }
            if (tvRating.getText() == "4")
            {
                tvRating.setBackgroundColor(Color.parseColor("#95ae1f"));
            }
            if (tvRating.getText() == "5")
            {
                tvRating.setBackgroundColor(Color.parseColor("#95ae1f"));
            }
            if (tvRating.getText() == "6")
            {
                tvRating.setBackgroundColor(Color.parseColor("#fbb601"));
            }
            if (tvRating.getText() == "7")
            {
                tvRating.setBackgroundColor(Color.parseColor("#fb9504"));
            }
            if (tvRating.getText() == "8")
            {
                tvRating.setBackgroundColor(Color.parseColor("#fb9504"));
            }
            if (tvRating.getText() == "9")
            {
                tvRating.setBackgroundColor(Color.parseColor("#fb9504"));
            }
            if (tvRating.getText() == "10")
            {
                tvRating.setBackgroundColor(Color.parseColor("#fa4c2a"));
            }
            //end of change color


            if (selectedPosition == position) {
                convertView.setSelected(true);
                convertView.setPressed(true);
                convertView.setBackgroundColor(Color.RED);
            } else {
                convertView.setSelected(false);
                convertView.setPressed(false);
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }
    }

    @Override
    public Loader<List<Trip>> onCreateLoader(int loaderID, Bundle bundle){
        return new AsyncTaskLoader<List<Trip>>(this) {
            boolean needReload = true;

            @Override
            public List<Trip> loadInBackground() {
                needReload = false;
                List<Trip> reversedTrips = new ArrayList<Trip>();
                for(Trip trip : dataSource.getAllTrips())
                    reversedTrips.add(0, trip);  // with latest record always on top
                return reversedTrips;
            }

            @Override
            public void deliverResult(List<Trip> trips) {
                super.deliverResult(trips);
            }

            @Override
            protected void onStartLoading() {
                if(needReload)
                    forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Trip>> loader, List<Trip> trips) {
        tripAdapter.addAll(trips);
    }

    @Override
    public void onLoaderReset(Loader<List<Trip>> loader) {
    }


    DriveDataSource dataSource;
    TripAdapter tripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_drives);

        dataSource = new DriveDataSource(this);
        dataSource.open();

        tripAdapter = new TripAdapter(
            this,
            R.layout.activity_analyze_drives_listview_row,
            new ArrayList<Trip>());

        final ListView tripsListView = (ListView) findViewById(R.id.drivesListView);
        tripsListView.setAdapter(tripAdapter);

        //Trigger activity DriveDetails by clicking item
        tripsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tripsListView.setItemChecked(position, true);
                Intent intent = new Intent(view.getContext(), DriveDetails.class);
                Trip trip = (Trip) tripsListView.getItemAtPosition(position);
                intent.putExtra("selectedTrip", trip);
                startActivityForResult(intent, 0);
            }
        });

        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                for(Trip trip : mSelectedTrips) {
                    dataSource.deleteTrip(trip.getId());
                    tripAdapter.remove(trip);
                }
                mSelectedTrips.clear();
            }
        });

        getSupportLoaderManager().initLoader(-1, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_analyze_drives, menu);
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
