package SQLiteDatabase;

/**
 * Created by jied on 24/04/15.
 */
public class Log {
    private String startTime;
    private String endTime;
    private double latitude;
    private double longitude;
    private long speed;
    private long RPM;
    private long fuelConsume;
    private String type;

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

    public long getSpeed(){
        return speed;
    }

    public void setSpeed(long speed){
        this.speed = speed;
    }

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

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
}
