package com.gu.gminions.db;

/**
 * Created by jied on 24/04/15.
 */
public class LocationInfo {
    private long locationId;
    private long tripId;
    private double latitude;
    private double longitude;
    private double altitude;

    public long getLocationId(){
        return locationId;
    }

    public void setLocationId(long id){
        this.locationId = id;
    }

    public long getTripId(){
        return tripId;
    }

    public void setTripId(long tripId){
        this.tripId = tripId;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getAltitude(){
        return altitude;
    }

    public void setAltitude(double altitude){
        this.altitude = altitude;
    }
}
