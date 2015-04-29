package com.aronssondev.andreas.drivetracker;
import android.support.v4.app.Fragment;

public class DriveActivity extends SingleFragmentActivity {
    /** A key for passing a drive ID as a long */
    public static final String EXTRA_RUN_ID = "RUN_ID";
    
    @Override
    protected Fragment createFragment() {
        long driveId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (driveId != -1) {
            return DriveFragment.newInstance(driveId);
        } else {
            return new DriveFragment();
        }
    }

}
