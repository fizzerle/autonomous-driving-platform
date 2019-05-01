package tuwien.dse.notificationstorageservice.dto;

import java.util.Date;

public class OemNotificationDto {

    private Date timestamp;
    private String description;
    private String chassisnumber;

    public OemNotificationDto() {

    }

    public OemNotificationDto(Date timestamp, String description, String chassisnumber) {
        this.timestamp = timestamp;
        this.description = description;
        this.chassisnumber = chassisnumber;
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
}
