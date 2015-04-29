package com.aronssondev.andreas.drivetracker;
import android.content.Context;
import android.location.Location;

class LastLocationLoader extends DataLoader<Location> {
    private long mDriveId;
    
    public LastLocationLoader(Context context, long driveId) {
        super(context);
        mDriveId = driveId;
    }

    @Override
    public Location loadInBackground() {
        return DriveManager.get(getContext()).getLastLocationForDrive(mDriveId);
    }
}