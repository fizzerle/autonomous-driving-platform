package tuwien.dse.notificationstorageservice.dto;

public class BlueLightOrgNotificationDto {

    private String crashId;
    private String oem;
    private String chassisnumber;
    private String modeltype;
    private Location location;
    private int passengers;

    public BlueLightOrgNotificationDto() {

    }

    public BlueLightOrgNotificationDto(String crashId, String oem, String chassisnumber, String modeltype, Location location, int passengers) {
        this.crashId = crashId;
        this.oem = oem;
        this.chassisnumber = chassisnumber;
        this.modeltype = modeltype;
        this.location = location;
        this.passengers = passengers;
    }

    public String getOem() {
        return oem;
    }

    public String getCrashId() {
        return crashId;
    }

    public void setCrashId(String crashId) {
        this.crashId = crashId;
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

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
}
