package com.aronssondev.drivetracker;

/**
 * Created by andreas on 16/05/15.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button trackDrivingBtn = (Button) findViewById(R.id.trackDriving_btn);
        trackDrivingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), DriveActivity.class);
                startActivityForResult(intent, 0);
            }
        });



        Button analyzeDrivesBtn = (Button) findViewById(R.id.analyzeDrives_btn);
        analyzeDrivesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View va) {

                Intent intent = new Intent(va.getContext(), DriveListActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }



}
