package tuwien.dse.entitystorageservice.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tuwien.dse.entitystorageservice.exception.CarAlreadyExistsException;
import tuwien.dse.entitystorageservice.model.Car;
import tuwien.dse.entitystorageservice.persistence.CarRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class EntityStorageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityStorageController.class);

    @Autowired
    CarRepository carRepository;

    @GetMapping("/test")
    public String test() {
        LOGGER.info("test called");
        return "test called entityservice";
    }

    @GetMapping("/entitystorage/test")
    public String test2() {
        LOGGER.info("entitystorage test called");
        return "entitystorage test";
    }


    @GetMapping("/entitystorage/cars")
    public List<Car> getAll(@RequestParam(required = false) Optional<String> oem) {
        if (oem.isPresent()) {
            return carRepository.findAllByOem(oem.get());
        }
        return carRepository.findAll();
    }

    @PostMapping("/entitystorage/cars")
    public Car insertCar(@RequestBody Car car) throws CarAlreadyExistsException {
        if (carRepository.findByChassisnumber(car.getChassisnumber()) != null) {
            String err = "Car with chassisnumber " + car.getChassisnumber() + " already exists!";
            LOGGER.error(err);
            throw new CarAlreadyExistsException(err);
        }
        return carRepository.save(car);
    }

    @GetMapping("/entitystorage/cars/{chassisnumber}")
    public Car getCarByChassisnumber(@PathVariable String chassisnumber) {
        return carRepository.findByChassisnumber(chassisnumber);
    }

    @DeleteMapping("/entitystorage/cars/{chassisnumber}")
    public Long deleteCarByChassisnumber(@PathVariable String chassisnumber) {
        return carRepository.deleteCarByChassisnumber(chassisnumber);
    }

    @GetMapping("/entitystorage/oem")
    public List<String> getOems() {
        return carRepository.findAll().stream().map(car -> car.getOem()).distinct().collect(Collectors.toList());
    }
}
