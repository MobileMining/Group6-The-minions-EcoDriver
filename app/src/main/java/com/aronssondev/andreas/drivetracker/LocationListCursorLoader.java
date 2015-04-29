package com.aronssondev.andreas.drivetracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by andreas on 27/04/15.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mDriveId;

    public LocationListCursorLoader(Context c, long driveId) {
        super(c);
        mDriveId = driveId;
    }

    @Override
    protected Cursor loadCursor() {
        return DriveManager.get(getContext()).queryLocationsForDrive(mDriveId);

    }
}
