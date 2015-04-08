package com.aronssondev.andreas.ecodriver;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import SQLiteDatabase.DriveRecord;
import SQLiteDatabase.DriveRecordsDataSource;


public class AnalyzeDrives extends ActionBarActivity {

    int mSelectedItemPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_drives);

        final DriveRecordsDataSource dataSource = new DriveRecordsDataSource(this);
        dataSource.open();

        //String[] drives = {"Drive1", "Drive2", "Drive3"};
        final ArrayAdapter drivesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataSource.getAllRecords());

        final ListView drivesListView =  (ListView) findViewById(R.id.drivesListView);
        drivesListView.setAdapter(drivesAdapter);

     //   drivesListView.setOnItemClickListener(
     //           new AdapterView.OnItemClickListener() {
     //               @Override
     //               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     //                   String drive = String.valueOf(parent.getItemAtPosition(position));
     //                   Toast.makeText(AnalyzeDrives.this, drive, Toast.LENGTH_LONG).show();
     //               }
     //         }
     //   );

        mSelectedItemPos = -1;

        drivesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                /*
                if (view instanceof TextView) {
                    TextView tv = (TextView)view;
                    tv.setText(tv.getText().toString() + " Selected");

                    if(mSelectedItemPos != -1) {
                       Object preSelected = parent.get(mSelectedItemPos);
                        if(preSelected instanceof TextView) {
                            TextView preView = (TextView) preSelected;
                            String preStr = preView.getText().toString();
                            preView.setText(preStr.substring(0, preStr.length() - 9));
                        }
                    }

                    mSelectedItemPos = position;
                }
                */

                mSelectedItemPos = position;
            }
        });


        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                DateFormat df = DateFormat.getDateTimeInstance();
                String record = df.format(new Date());

                String[] places = {"Stockholm", "Göteborg", "Malmö"};
                Random rand = new Random();
                String startPlace = places[rand.nextInt(3)];
                String destination = places[rand.nextInt(3)];
                DriveRecord dr = dataSource.createRecord(record, startPlace, destination,
                        0, null, 0, 0, 0, 0);

                drivesAdapter.add(dr);
                drivesAdapter.notifyDataSetChanged();  //adapter has been changed.
                mSelectedItemPos = -1;
            }
        });

        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(mSelectedItemPos != -1){
                    DriveRecord dr = (DriveRecord) drivesAdapter.getItem(mSelectedItemPos);
                    dataSource.deleteRecord(dr);
                    drivesAdapter.remove(dr);
                    mSelectedItemPos = -1;
                }
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
