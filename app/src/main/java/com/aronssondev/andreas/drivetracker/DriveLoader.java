package com.aronssondev.andreas.drivetracker;
import android.content.Context;

class DriveLoader extends DataLoader<Drive> {
    private long mDriveId;
    
    public DriveLoader(Context context, long driveId) {
        super(context);
        mDriveId = driveId;
    }
    
    @Override
    public Drive loadInBackground() {
        return DriveManager.get(getContext()).getDrive(mDriveId);
    }
}