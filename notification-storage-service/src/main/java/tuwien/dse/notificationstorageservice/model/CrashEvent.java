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
    private Date setInactiveTimestamp;
    private String description;

    public CrashEvent() {

    }

    public CrashEvent(String crashId, String eventId, String chassisnumber, Date crashTimestamp, Date setInactiveTimestamp, String description) {
        this.crashId = crashId;
        this.eventId = eventId;
        this.chassisnumber = chassisnumber;
        this.crashTimestamp = crashTimestamp;
        this.setInactiveTimestamp = setInactiveTimestamp;
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

    public void setCrashTimestamp(Date chrashTimestamp) {
        this.crashTimestamp = chrashTimestamp;
    }

    public Date getSetInactiveTimestamp() {
        return setInactiveTimestamp;
    }

    public void setSetInactiveTimestamp(Date setInactiveTimestamp) {
        this.setInactiveTimestamp = setInactiveTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
