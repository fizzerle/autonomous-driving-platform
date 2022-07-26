package tuwien.dse.notificationstorageservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "notifications")
public class CrashEvent {

    @Id
    private String crashId;
    private String eventId;
    private String chassisnumber;
    private Date crashTimestamp;
    private Date resolveTimestamp;
    private String description;

    /**
     * Default-Constructor for a crashEvent.
     */
    public CrashEvent() {

    }

    /**
     * Constructor for a CrashEvent-model managed in the database.
     *
     * @param eventId          Id of the linked event.
     * @param chassisnumber    Number identifying the crashed car's chassis
     * @param crashTimestamp   Timestamp describing when the crash happened.
     * @param resolveTimestamp Timestamp describing when the crash was resolved.
     * @param description      Description of the crash reasons.
     */
    public CrashEvent(String eventId, String chassisnumber, Date crashTimestamp, Date resolveTimestamp, String description) {
        this.eventId = eventId;
        this.chassisnumber = chassisnumber;
        this.crashTimestamp = crashTimestamp;
        this.resolveTimestamp = resolveTimestamp;
        this.description = description;
    }

    public String getCrashId() {
        return crashId;
    }

    public void setCrashId(String crashId) {
        this.crashId = crashId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getChassisnumber() {
        return chassisnumber;
    }

    public void setChassisnumber(String chassisnumber) {
        this.chassisnumber = chassisnumber;
    }

    public Date getCrashTimestamp() {
        return crashTimestamp;
    }

    public void setCrashTimestamp(Date crashTimestamp) {
        this.crashTimestamp = crashTimestamp;
    }

    public Date getResolveTimestamp() {
        return resolveTimestamp;
    }

    public void setResolveTimestamp(Date resolveTimestamp) {
        this.resolveTimestamp = resolveTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
