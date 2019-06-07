package tuwien.dse.notificationstorageservice.dto;

import java.util.Date;

public class CrashEventDto {

    private String chassisNumber;
    private String eventId;
    private Date timestamp;
    private String description;

    /**
     * Default-constructor for a crashEventDto.
     */
    public CrashEventDto() {

    }

    /**
     * Constructor for a CrashEventDto which represents a event where a crash happened.
     *
     * @param chassisNumber Number identifying the car's chassis.
     * @param eventId       Id of the event linked to this crashEvent.
     * @param timestamp     Timestamp describing when the crash happened.
     * @param description   Description of what happened.
     */
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
