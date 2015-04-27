package SQLiteDatabase;

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
    private String timeDuration;
    private long startMileage;
    private long endMileage;
    private long distance;
    private String startPlace;
    private String destination;
    private long avgSpeed;
    private long avgRPM;
    private long fuelConsume;
    private long emissionCO2;
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

    public String getTimeDuration(){
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration){
        this.timeDuration = timeDuration;
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

    public long getDistance(){
        return distance;
    }

    public void setDistance(long distance){
        this.distance = distance;
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

    public long getRating(){
        return rating;
    }

    public void setRating(long rating){
        this.rating = rating;
    }

    public String toString(){
        return startTime + " " + startPlace + " - " + destination;
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
        dest.writeString(timeDuration);
        dest.writeLong(startMileage);
        dest.writeLong(endMileage);
        dest.writeLong(distance);
        dest.writeString(startPlace);
        dest.writeString(destination);
        dest.writeLong(avgSpeed);
        dest.writeLong(avgRPM);
        dest.writeLong(fuelConsume);
        dest.writeLong(emissionCO2);
        dest.writeLong(rating);
    }

    Trip(Parcel source){
        id = source.readLong();
        startTime = source.readString();
        endTime = source.readString();
        timeDuration = source.readString();
        startMileage = source.readLong();
        endMileage = source.readLong();
        distance = source.readLong();
        startPlace = source.readString();
        destination = source.readString();
        avgSpeed = source.readLong();
        avgRPM = source.readLong();
        fuelConsume = source.readLong();
        emissionCO2 = source.readLong();
        rating = source.readLong();
    }

    Trip(){}

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
