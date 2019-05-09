package tuwien.dse.eventstorageservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String chassisnumber;
    private Date timestamp;
    private String crashEvent;
    private Location location;
    private int speed;
    private double spaceAhead;
    private double spaceBehind;
    private int passengers;

    public Event() {

    }

    public Event(Location location, String chassisnumber, Date timestamp, int speed, double spaceAhead, double spaceBehind,String crashEvent, int passengers) {
        this.location = location;
        this.chassisnumber = chassisnumber;
        this.timestamp = timestamp;
        this.speed = speed;
        this.spaceAhead = spaceAhead;
        this.spaceBehind = spaceBehind;
        this.crashEvent = crashEvent;
        this.passengers = passengers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChassisnumber() {
        return chassisnumber;
    }

    public void setChassisnumber(String chassisnumber) {
        this.chassisnumber = chassisnumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCrashEvent() {
        return crashEvent;
    }

    public void setCrashEvent(String crashEvent) {
        this.crashEvent = crashEvent;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getSpaceAhead() {
        return spaceAhead;
    }

    public void setSpaceAhead(double spaceAhead) {
        this.spaceAhead = spaceAhead;
    }

    public double getSpaceBehind() {
        return spaceBehind;
    }

    public void setSpaceBehind(double spaceBehind) {
        this.spaceBehind = spaceBehind;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
}
