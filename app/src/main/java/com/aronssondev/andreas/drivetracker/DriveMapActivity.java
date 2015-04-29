package com.aronssondev.andreas.drivetracker;

import android.app.Fragment;

/**
 * Created by andreas on 19/04/15.
 */
public class DriveMapActivity extends SingleFragmentActivity {
    //pass a ID as a long
    public static final String EXTRA_RUN_ID = "com.aronssondev.andreas.drivetracker.drive_id";


    @Override
    protected android.support.v4.app.Fragment createFragment() {
        long driveId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (driveId != -1) {

            return DriveMapFragment.newInstance(driveId);

        }  else {
            return new DriveMapFragment();
        }
    }

}
