package tuwien.dse.notificationstorageservice.dto;

public class CarNotificationDto {

    private Location location;
    private boolean active;

    public CarNotificationDto() {

    }

    public CarNotificationDto(Location location, boolean active) {
        this.location = location;
        this.active = active;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
