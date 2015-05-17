package com.aronssondev.drivetracker.db;

/**
 * Created by jied on 24/04/15.
 */
public class Warning {
    private long warningId;
    private long tripId;
    private String time;
    private double latitude;
    private double longitude;
    private double altitude;
    private long speed;
    //private long RPM;
    //private long fuelConsume;
    private String type;

    public long getWarningId(){
        return warningId;
    }

    public void setWarningId(long warningId){
        this.warningId = warningId;
    }

    public long getTripId(){
        return tripId;
    }

    public void setTripId(long tripId){
        this.tripId = tripId;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
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

    public long getSpeed(){
        return speed;
    }

    public void setSpeed(long speed){
        this.speed = speed;
    }

    /*
    public long getRPM(){
        return RPM;
    }

    public void setRPM(long RPM){
        this.RPM = RPM;
    }

    public long getFuelConsume(){
        return fuelConsume;
    }

    public void setFuelConsume(long fuelConsume){
        this.fuelConsume = fuelConsume;
    }
    */

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
}
