package com.aronssondev.andreas.ecodriver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import SQLiteDatabase.DriveRecord;
import SQLiteDatabase.DriveRecordsDataSource;


public class AnalyzeDrives extends ActionBarActivity {

    Set<DriveRecord> mSelectedRecords = new TreeSet<DriveRecord>();

    private class DriveRecordAdapter extends ArrayAdapter<DriveRecord>{
        private int resource;
        private LayoutInflater inflater;
        private Context context;

        public DriveRecordAdapter(Context cxt, int resourceId, List<DriveRecord> objects){
            super(cxt, resourceId, objects);
            resource = resourceId;
            inflater = LayoutInflater.from(cxt);
            context = cxt;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            convertView = (LinearLayout) inflater.inflate(resource, null);
            final DriveRecord driveRecord = getItem(position);

            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            if(mSelectedRecords.contains(driveRecord))
                checkBox.setChecked(true);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                        if(isChecked)
                            mSelectedRecords.add(driveRecord);
                        else
                            mSelectedRecords.remove(driveRecord);
                    }
                });

            TextView timeRecord = (TextView) convertView.findViewById(R.id.timeRecord);
            timeRecord.setText(driveRecord.getDriveRecord());

            TextView places = (TextView) convertView.findViewById(R.id.placeRecord);
            places.setText(driveRecord.getStartPlace() + " - " + driveRecord.getDestination());

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_drives);

        final DriveRecordsDataSource dataSource = new DriveRecordsDataSource(this);
        dataSource.open();

<<<<<<< HEAD
=======
        //String[] drives = {"Drive1", "Drive2", "Drive3"}; Test
>>>>>>> 88251fc0993a26b6e0485483520468f666ea3b08
        final DriveRecordAdapter driveRecordAdapter = new DriveRecordAdapter(
                this,
                R.layout.activity_analyze_drives_listview_row,
                dataSource.getAllRecords());

        final ListView drivesListView =  (ListView) findViewById(R.id.drivesListView);
        drivesListView.setAdapter(driveRecordAdapter);

     /*   drivesListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String drive = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(AnalyzeDrives.this, drive, Toast.LENGTH_LONG).show();
                    }
              }
        );
*/
        drivesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                Intent intent = new Intent(view.getContext(), DriveDetails.class);
                startActivityForResult(intent, 0);
            }
        });


        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                DateFormat df = DateFormat.getDateTimeInstance();
                String record = df.format(new Date());

                String[] places = {"Stockholm", "Göteborg", "Malmö", "Borås", "Varberg", "Karlstad" ,"Helsingborg"};
                Random rand = new Random();
                String startPlace = places[rand.nextInt(7)];
                String destination = places[rand.nextInt(7)];
                DriveRecord dr = dataSource.createRecord(record, startPlace, destination,
                        0, null, 0, 0, 0, 0);

                driveRecordAdapter.add(dr);
                driveRecordAdapter.notifyDataSetChanged();  //adapter has been changed.
            }
        });

        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                for(DriveRecord dr : mSelectedRecords) {
                    dataSource.deleteRecord(dr);
                    driveRecordAdapter.remove(dr);
                }
                mSelectedRecords.clear();
            }
        });
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
