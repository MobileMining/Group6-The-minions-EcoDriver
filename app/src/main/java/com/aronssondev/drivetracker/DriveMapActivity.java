package com.aronssondev.drivetracker;


import android.support.v4.app.Fragment;

public class DriveMapActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DriveMapFragment();
    }
}
