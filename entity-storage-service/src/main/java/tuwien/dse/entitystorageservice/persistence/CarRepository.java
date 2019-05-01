package tuwien.dse.entitystorageservice.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import tuwien.dse.entitystorageservice.model.Car;

import java.util.List;

public interface CarRepository extends MongoRepository<Car, String> {

    Long deleteCarByChassisnumber(String chassisnumber);
    Car findByChassisnumber(String chassisnumber);
    List<Car> findAllByOem(String oem);
    List<Car> findAll();
}
