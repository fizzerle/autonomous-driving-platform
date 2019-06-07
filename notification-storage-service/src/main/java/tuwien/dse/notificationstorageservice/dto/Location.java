package tuwien.dse.notificationstorageservice.dto;

public class Location {

    private double lat;
    private double lng;

    /**
     * Default-constructor for location.
     */
    public Location() {

    }

    /**
     * Consturctor for a location object which wraps longitude and latitude values for a location.
     *
     * @param lat Latitude
     * @param lng Longitude
     */
    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
