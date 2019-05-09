package tuwien.dse.eventstorageservice.dto;

import java.util.Date;

public class CrashEventDto {

    private String chassisNumber;
    private String eventId;
    private Date timestamp;
    private String description;

    public CrashEventDto() {

    }

    public CrashEventDto(String chassisNumber, String eventId, Date timestamp, String description) {
        this.chassisNumber = chassisNumber;
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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
}
