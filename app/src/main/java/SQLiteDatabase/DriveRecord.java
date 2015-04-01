package SQLiteDatabase;

/**
 * Created by jied on 01/04/15.
 * Tuple of table in database.
 */
public class DriveRecord {
    private long id;
    private String driveRecord;

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

    public String toString(){
        return driveRecord;
    }
}
