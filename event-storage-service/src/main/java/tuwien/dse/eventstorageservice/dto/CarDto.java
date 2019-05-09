package tuwien.dse.eventstorageservice.dto;

public class CarDto {

    private String chassisnumber;
    private String oem;
    private String modelType;

    public CarDto() {

    }

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
