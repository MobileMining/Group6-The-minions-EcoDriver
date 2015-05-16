package com.gu.gminions.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jied on 01/04/15.
 * Tuple of table in database.
 */
public class Trip implements Comparable<Trip>, Parcelable {
    private long id;
    private String startTime;
    private String endTime;
    private long duration;
    private long startMileage;
    private long endMileage;
    private long fuelConsume;
    private long totalWarning;
    private long rating;

    public int compareTo(Trip other) {
        return (int)(id - other.id);
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getStartTime(){
        return startTime;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getEndTime(){
        return endTime;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartMileage(){
        return startMileage;
    }

    public void setStartMileage(long startMileage){
        this.startMileage = startMileage;
    }

    public long getEndMileage(){
        return endMileage;
    }

    public void setEndMileage(long endMileage){
        this.endMileage = endMileage;
    }

    public long getFuelConsume(){
        return fuelConsume;
    }

    public void setFuelConsume(long fuelConsume){
        this.fuelConsume = fuelConsume;
    }

    public long getTotalWarning(){
        return totalWarning;
    }

    public void setTotalWarning(long totalWarning){
        this.totalWarning = totalWarning;
    }

    public long getRating(){
        return rating;
    }

    public void setRating(long rating){
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeLong(duration);
        dest.writeLong(startMileage);
        dest.writeLong(endMileage);
        dest.writeLong(fuelConsume);
        dest.writeLong(totalWarning);
        dest.writeLong(rating);
    }

    Trip(Parcel source){
        id = source.readLong();
        startTime = source.readString();
        endTime = source.readString();
        duration = source.readLong();
        startMileage = source.readLong();
        endMileage = source.readLong();
        fuelConsume = source.readLong();
        totalWarning = source.readLong();
        rating = source.readLong();
    }

    public Trip(){}

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Object createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Trip[size];
        }
    };
}
