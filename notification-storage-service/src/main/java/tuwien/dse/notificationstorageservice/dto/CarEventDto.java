package tuwien.dse.notificationstorageservice.dto;

public class CarEventDto {

    private String oem;
    private String chassisNumber;
    private String modeltype;
    private int passengers;
    private Location location;
    private int speed;
    private double spaceAhead;
    private double spaceBehind;
    private String crashEvent;

    /**
     * Default-constructor for the CarEventDto
     */
    public CarEventDto() {
    }

    /**
     * Constructor for a CarEventDto which describes a event.
     * A event describes the possition of the car and additional information.
     *
     * @param oem           The OEM of the car.
     * @param chassisNumber Number describing the car's chassis.
     * @param modeltype     Type description of the car.
     * @param passengers    Number of passengers in the car.
     * @param location      Location where the car is described by latitude and longitude.
     * @param speed         Speed in km/h.
     * @param spaceAhead    Space ahead of the car measured by a sensor.
     * @param spaceBehind   Space behind the car measured by a sensor.
     * @param crashEvent    Description of a crash event, only set if this event represents a crash.
     */
    public CarEventDto(String oem, String chassisNumber, String modeltype, int passengers, Location location, int speed, double spaceAhead, double spaceBehind, String crashEvent) {
        this.oem = oem;
        this.chassisNumber = chassisNumber;
        this.modeltype = modeltype;
        this.passengers = passengers;
        this.location = location;
        this.speed = speed;
        this.spaceAhead = spaceAhead;
        this.spaceBehind = spaceBehind;
        this.crashEvent = crashEvent;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getModeltype() {
        return modeltype;
    }

    public void setModeltype(String modeltype) {
        this.modeltype = modeltype;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
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

    public String getCrashEvent() {
        return crashEvent;
    }

    public void setCrashEvent(String crashEvent) {
        this.crashEvent = crashEvent;
    }
}
