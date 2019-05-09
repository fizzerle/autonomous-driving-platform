package tuwien.dse.entitystorageservice.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
        return "test called";
    }

    @GetMapping("/entitystorage/test")
    public String test2() {
        LOGGER.info("entitystorage test called");
        return "entitystorage test";
    }


    @GetMapping("/cars")
    public List<Car> getAll(@RequestParam(required = false) Optional<String> oem) {
        if (oem.isPresent()) {
            return carRepository.findAllByOem(oem.get());
        }
        return carRepository.findAll();
    }

    @PostMapping("/cars")
    public Car insertCar(@RequestBody Car car) {
        return carRepository.save(car);
    }

    @GetMapping("/cars/{chassisnumber}")
    public Car getCarByChassisnumber(@PathVariable String chassisnumber) {
        return carRepository.findByChassisnumber(chassisnumber);
    }

    @DeleteMapping("/cars/{chassisnumber}")
    public Long deleteCarByChassisnumber(@PathVariable String chassisnumber) {
        return carRepository.deleteCarByChassisnumber(chassisnumber);
    }

    @GetMapping("/oem")
    public List<String> getOems() {
        return carRepository.findAll().stream().map(car -> car.getOem()).distinct().collect(Collectors.toList());
    }
}
