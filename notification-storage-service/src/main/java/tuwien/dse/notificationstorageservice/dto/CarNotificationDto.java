package tuwien.dse.notificationstorageservice.dto;

public class CarNotificationDto {

    private Location location;
    private boolean active;

    /**
     * Default-constructor for the carNotificationDto.
     */
    public CarNotificationDto() {

    }

    /**
     * Constructor for a Dto with information about a crash, the car can see.
     *
     * @param location Location of the crash.
     * @param active   Boolean describing if the crash is active.
     */
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
