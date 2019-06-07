package tuwien.dse.notificationstorageservice.dto;

import java.util.Date;

public class OemNotificationDto {

    private String crashId;
    private Date timestamp;
    private String description;
    private String chassisnumber;
    private Date resolveTimestamp;
    private Location location;

    /**
     * Default-Constructor for the OemNotificationDto.
     */
    public OemNotificationDto() {

    }

    /**
     * Constructor for a Dto with the crashInformation a OEM is allowed to see.
     *
     * @param crashId          Id of the crash.
     * @param timestamp        Timestamp describing when the crash happened.
     * @param description      Description of the crash reasons.
     * @param chassisnumber    Number identifying the crashed car's chassis.
     * @param resolveTimestamp Timestamp describing when the crash was resolved. (Null for unresolved crashes)
     * @param location         Location of the crash.
     */
    public OemNotificationDto(String crashId, Date timestamp, String description, String chassisnumber, Date resolveTimestamp, Location location) {
        this.crashId = crashId;
        this.timestamp = timestamp;
        this.description = description;
        this.chassisnumber = chassisnumber;
        this.resolveTimestamp = resolveTimestamp;
        this.location = location;
    }

    public String getCrashId() {
        return crashId;
    }

    public void setCrashId(String crashId) {
        this.crashId = crashId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChassisnumber() {
        return chassisnumber;
    }

    public void setChassisnumber(String chassisnumber) {
        this.chassisnumber = chassisnumber;
    }

    public Date getResolveTimestamp() {
        return resolveTimestamp;
    }

    public void setResolveTimestamp(Date resolveTimestamp) {
        this.resolveTimestamp = resolveTimestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
