package tuwien.dse.entitystorageservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tuwien.dse.entitystorageservice.model.Car;
import tuwien.dse.entitystorageservice.persistence.CarRepository;

import java.util.List;

@RestController
public class EntityStorageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityStorageController.class);

    @Autowired
    CarRepository carRepository;

    @GetMapping("/cars/")
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @GetMapping("/test/")
    public String test() {
        LOGGER.info("test called");
        return "test called";
    }


    @GetMapping("/cars/{oem}")
    public List<Car> getAllOfOem(@PathVariable String oem) {
        return carRepository.findAllByOem(oem);
    }

    @PostMapping("/saveCar/")
    public Car insertCar(@RequestBody Car car) {
        return carRepository.save(car);
    }

    @GetMapping("/car/{chassisnumber}")
    public Car getCarByChassisnumber(@PathVariable String chassisnumber) {
        return carRepository.findByChassisnumber(chassisnumber);
    }

    @DeleteMapping("/car/{chassisnumber}")
    public Long deleteCarByChassisnumber(@PathVariable String chassisnumber) {
        return carRepository.deleteCarByChassisnumber(chassisnumber);
    }
}
