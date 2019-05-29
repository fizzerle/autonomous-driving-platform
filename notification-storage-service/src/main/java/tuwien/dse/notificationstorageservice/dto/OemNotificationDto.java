package tuwien.dse.notificationstorageservice.dto;

import java.util.Date;

public class OemNotificationDto {

    private String crashId;
    private Date timestamp;
    private String description;
    private String chassisnumber;
    private Date resolveTimestamp;
    private Location location;

    public OemNotificationDto() {

    }

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
