package com.aronssondev.drivetracker;


import android.support.v4.app.Fragment;

public class DriveListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DriveListFragment();
    }
}
