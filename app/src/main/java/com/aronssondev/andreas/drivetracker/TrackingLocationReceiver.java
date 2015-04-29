package com.aronssondev.andreas.drivetracker;
import android.content.Context;
import android.location.Location;

public class TrackingLocationReceiver extends LocationReceiver {
    
    @Override
    protected void onLocationReceived(Context c, Location loc) {
        DriveManager.get(c).insertLocation(loc);
    }

}
