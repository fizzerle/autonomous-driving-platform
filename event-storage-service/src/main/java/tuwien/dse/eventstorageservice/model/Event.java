package tuwien.dse.eventstorageservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String chassisnumber;
    private Date timestamp;
    private String crashEvent;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;
    private int speed;
    private double spaceAhead;
    private double spaceBehind;
    private int passengers;

    /**
     * Default Constructor for a Event.
     */
    public Event() {

    }

    /**
     * Event-model describing a event which is managed in the database.
     * A event is one set of data sent by the car, including location, speed, sensor-data....
     *
     * @param location      Location where the car was at this event.
     * @param chassisnumber Number identifying the car's chassis.
     * @param timestamp     Timestamp describing when this event happened/was sent.
     * @param speed         Speed in km/h.
     * @param spaceAhead    Space ahead of the car measured by a sensor.
     * @param spaceBehind   Space behind the car measured by a sensor.
     * @param crashEvent    Description of a crash event, only set if this event represents a crash.
     * @param passengers    Number of passengers in the car.
     */
    public Event(Location location, String chassisnumber, Date timestamp, int speed, double spaceAhead, double spaceBehind, String crashEvent, int passengers) {
        this.location = new GeoJsonPoint(location.getLng(), location.getLat());
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

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
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
