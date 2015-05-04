package com.aronssondev.andreas.drivetracker;

import android.support.v4.app.Fragment;


public class DriveActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DriveFragment();
    }
}
