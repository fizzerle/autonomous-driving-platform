package tuwien.dse.eventstorageservice.dto;

public class CarDto {

    private String chassisnumber;
    private String oem;
    private String modelType;

    /**
     * Default constructor for a carDto.
     */
    public CarDto() {

    }

    /**
     * Constructor for a carDto.
     *
     * @param chassisnumber Number which identifies the chassis of the car
     * @param oem           Oem which produced the car
     * @param modelType     Model description of the car
     */
    public CarDto(String chassisnumber, String oem, String modelType) {
        this.chassisnumber = chassisnumber;
        this.oem = oem;
        this.modelType = modelType;
    }

    public String getChassisnumber() {
        return chassisnumber;
    }

    public void setChassisnumber(String chassisnumber) {
        this.chassisnumber = chassisnumber;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
