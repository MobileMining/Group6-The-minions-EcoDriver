package com.aronssondev.drivetracker;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Drive {
    private long mId;
    private Date mStartDate;

    public Drive() {
        mStartDate = new Date();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public int getDurationSeconds(long endMillis) {
        return (int) ((endMillis - mStartDate.getTime()) / 1000);
    }

    public static String formatDuration(int durationSeconds) {
        int seconds = durationSeconds % 60;
        int minutes = (durationSeconds / 60) % 60;
        int hours = durationSeconds / 3600;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getFormattedDate() {
        String format = "EEEE, MMM d, yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(mStartDate);
    }
}