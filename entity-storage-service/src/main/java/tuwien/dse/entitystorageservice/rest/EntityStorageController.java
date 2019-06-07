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

    @GetMapping("/entitystorage/endless")
    public String endlesshystrixcall() throws InterruptedException {
        LOGGER.info("Start of endless hystrix call");
        Thread.sleep(10000);
        LOGGER.info("End of endless hystrix call");
        return "i finally processed your request";
    }


    /**
     * Rest-Endpoint to get all cars in the database.
     * Optionally the cars can be filtered by the OEM.
     *
     * @param oem The oem for which the cars are searched.
     * @return List of cars. (Filtered, if OEM argument was passed)
     */
    @GetMapping("/entitystorage/cars")
    public List<Car> getAll(@RequestParam(required = false) Optional<String> oem) {
        ;
        if (oem.isPresent()) {
            LOGGER.info("Getting all cars of oem: " + oem);
            return carRepository.findAllByOem(oem.get());
        }
        LOGGER.info("Getting all cars...");
        return carRepository.findAll();
    }

    /**
     * Rest-Endpoint to insert a car into the database.
     * @param car Car to be inserted.
     * @return Inserted car
     * @throws CarAlreadyExistsException If a car with the same chassisnumber already exists.
     */
    @PostMapping("/entitystorage/cars")
    public Car insertCar(@RequestBody Car car) throws CarAlreadyExistsException {
        if (carRepository.findByChassisnumber(car.getChassisnumber()) != null) {
            String err = "Car with chassisnumber " + car.getChassisnumber() + " already exists!";
            LOGGER.error(err);
            throw new CarAlreadyExistsException(err);
        }
        LOGGER.info("Saving new car...");
        Car saveCar = carRepository.save(car);
        LOGGER.info("Saved new car: " + saveCar.getChassisnumber());
        return saveCar;
    }

    /**
     * Rest-Endpoint to get a car by its chassisnumber.
     * @param chassisnumber The chassisnumber of the car that is searched.
     * @return The car requested.
     */
    @GetMapping("/entitystorage/cars/{chassisnumber}")
    public Car getCarByChassisnumber(@PathVariable String chassisnumber) {
        LOGGER.info("Searching car " + chassisnumber);
        return carRepository.findByChassisnumber(chassisnumber);
    }

    /**
     * Rest-Endpoint to delete cars identified by their chassisnumber.
     * @param chassisnumber The identifier of the car that should be deleted.
     * @return 1 if car was found and deleted, 0 else
     */
    @DeleteMapping("/entitystorage/cars/{chassisnumber}")
    public Long deleteCarByChassisnumber(@PathVariable String chassisnumber) {
        LOGGER.info("Deleting car " + chassisnumber);
        return carRepository.deleteCarByChassisnumber(chassisnumber);
    }

    /**
     * Rest-Endpoint to get all OEMs that have cars saved in the database
     * @return List of all existing OEMs
     */
    @GetMapping("/entitystorage/oem")
    public List<String> getOems() {
        LOGGER.info("Searching all distinct oems in db...");
        return carRepository.findAll().stream().map(car -> car.getOem()).distinct().collect(Collectors.toList());
    }
}
