package tuwien.dse.notificationstorageservice.dto;

import java.util.Date;

public class BlueLightOrgNotificationDto {

    private String crashId;
    private String oem;
    private String chassisnumber;
    private String modeltype;
    private Location location;
    private Date resolveTimestamp;
    private Date timestamp;
    private int passengers;

    /**
     * Default-constructor for the BlueLightOrgNotificationDto.
     */
    public BlueLightOrgNotificationDto() {

    }

    /**
     * Constructor for a DTO containing all the information about a crash which a BlueLightOrg is allowed to see.
     *
     * @param crashId          Id of the crash.
     * @param oem              OEM of the crashed car.
     * @param chassisnumber    Number identifying the crashed car's chassis.
     * @param modeltype        Model of the crashed car.
     * @param location         Location of the crash.
     * @param resolveTimestamp Timestamp describing when the crash was resolved. (Null for unresolved crashes)
     * @param timestamp        Timestamp describing when the crash happened.
     * @param passengers       Number of passengers in the crashed car.
     */
    public BlueLightOrgNotificationDto(String crashId, String oem, String chassisnumber, String modeltype, Location location, Date resolveTimestamp, Date timestamp, int passengers) {
        this.crashId = crashId;
        this.oem = oem;
        this.chassisnumber = chassisnumber;
        this.modeltype = modeltype;
        this.location = location;
        this.resolveTimestamp = resolveTimestamp;
        this.timestamp = timestamp;
        this.passengers = passengers;
    }

    public String getCrashId() {
        return crashId;
    }

    public void setCrashId(String crashId) {
        this.crashId = crashId;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
    }

    public String getChassisnumber() {
        return chassisnumber;
    }

    public void setChassisnumber(String chassisnumber) {
        this.chassisnumber = chassisnumber;
    }

    public String getModeltype() {
        return modeltype;
    }

    public void setModeltype(String modeltype) {
        this.modeltype = modeltype;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getResolveTimestamp() {
        return resolveTimestamp;
    }

    public void setResolveTimestamp(Date resolveTimestamp) {
        this.resolveTimestamp = resolveTimestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
}
