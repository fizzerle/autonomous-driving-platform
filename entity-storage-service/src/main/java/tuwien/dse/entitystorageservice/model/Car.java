package tuwien.dse.entitystorageservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "entities")
public class Car {

    @Id
    private String chassisnumber;
    private String oem;
    private String modelType;

    /**
     * Default constructor for car.
     */
    public Car() {

    }

    /**
     * Constructor for a car-entity.
     *
     * @param chassisnumber Number which identifies the chassis of the car
     * @param oem           Oem which produced the car
     * @param modelType     Model description of the car
     */
    public Car(String chassisnumber, String oem, String modelType) {
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
