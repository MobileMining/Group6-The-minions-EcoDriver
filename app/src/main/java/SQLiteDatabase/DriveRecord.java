package SQLiteDatabase;

/**
 * Created by jied on 01/04/15.
 * Tuple of table in database.
 */
public class DriveRecord {
    private long id;
    private String driveRecord;
    private String startPlace;
    private String destination;
    private long distance;
    private String timeDuration;
    private long avgSpeed;
    private long avgRPM;
    private long fuelConsume;
    private long emissionCO2;


    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getDriveRecord(){
        return driveRecord;
    }

    public void setDriveRecord(String driveRecord){
        this.driveRecord = driveRecord;
    }

    public String getStartPlace(){
        return startPlace;
    }

    public void setStartPlace(String startPlace){
        this.startPlace = startPlace;
    }

    public String getDestination(){
        return destination;
    }

    public void setDestination(String destination){
        this.destination = destination;
    }

    public long getDistance(){
        return distance;
    }

    public void setDistance(long distance){
        this.distance = distance;
    }

    public String getTimeDuration(){
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration){
        this.timeDuration = timeDuration;
    }

    public long getAvgSpeed(){
        return avgSpeed;
    }

    public void setAvgSpeed(long avgSpeed){
        this.avgSpeed = avgSpeed;
    }

    public long getAvgRPM(){
        return avgRPM;
    }

    public void setAvgRPM(long avgRPM){
        this.avgRPM = avgRPM;
    }

    public long getFuelConsume(){
        return fuelConsume;
    }

    public void setFuelConsume(long fuelConsume){
        this.fuelConsume = fuelConsume;
    }

    public long getEmissionCO2(){
        return emissionCO2;
    }

    public void setEmissionCO2(long emissionCO2){
        this.emissionCO2 = emissionCO2;
    }

    public String toString(){
        return driveRecord + " " + startPlace + " " + destination;
    }
}
