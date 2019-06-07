package tuwien.dse.entitystorageservice.rest;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tuwien.dse.entitystorageservice.EntitystorageserviceApplication;
import tuwien.dse.entitystorageservice.exception.CarAlreadyExistsException;
import tuwien.dse.entitystorageservice.model.Car;
import tuwien.dse.entitystorageservice.persistence.CarRepository;

import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EntitystorageserviceApplication.class)
@TestPropertySource(locations = "classpath:application-dev.properties")
public class EntityStorageControllerTest {

    @Autowired
    private EntityStorageController entityStorageController;

    @Autowired
    private CarRepository carRepo;

    @Before
    public void setup() {
        insertCarTestData();
    }

    @Test
    public void testGetAllWithoutOemArgument_ShouldReturnAllCars() {
        List<Car> cars = entityStorageController.getAll(Optional.empty());
        Assert.assertEquals(6, cars.size());
    }

    @Test
    public void testGetAllWithOemArgumentAudi_ShouldReturnAllCars() {
        List<Car> cars = entityStorageController.getAll(Optional.of("Audi"));
        Assert.assertEquals(3, cars.size());
    }

    @Test
    public void testInsertCar_ShouldInsertCar() throws CarAlreadyExistsException {
        entityStorageController.insertCar(new Car("123", "Toyota", "Supra"));
        Assert.assertEquals(7, carRepo.findAll().size());
    }

    @Test(expected = CarAlreadyExistsException.class)
    public void testInsertCarWithExistingNumber_ShouldThrowException() throws CarAlreadyExistsException {
        entityStorageController.insertCar(new Car("000", "Audi", "A8"));
    }

    @Test
    public void testGetOems_ShouldReturnOems() {
        List<String> oems = entityStorageController.getOems();
        Assert.assertTrue(oems.contains("Audi"));
        Assert.assertTrue(oems.contains("BMW"));
        Assert.assertTrue(oems.contains("Opel"));
        Assert.assertEquals(3, oems.size());
    }


    @After
    public void cleanup() {
        carRepo.deleteAll();
    }

    private void insertCarTestData() {
        carRepo.deleteAll();
        carRepo.save(new Car("000", "Audi", "A8"));
        carRepo.save(new Car("001", "BMW", "i8 Coupe"));
        carRepo.save(new Car("002", "Opel", "Astra"));
        carRepo.save(new Car("003", "Audi", "Q2"));
        carRepo.save(new Car("004", "Audi", "r8"));
        carRepo.save(new Car("005", "BMW", "x7"));
    }
}
