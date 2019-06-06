package tuwien.dse.entitystorageservice.persistence;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tuwien.dse.entitystorageservice.model.Car;
import tuwien.dse.entitystorageservice.persistence.CarRepository;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepo;

    @Before
    public void setup() {
        insertCarTestData();
    }

    @Test
    public void testDeleteCarByChassinumber_shouldDeleteCar() {
        List<Car> cars = carRepo.findAll();
        Assert.assertEquals(6, cars.size());
        Assert.assertTrue(carListContainsCarWithChassisnumber(cars,"000"));

        carRepo.deleteCarByChassisnumber("000");

        cars = carRepo.findAll();
        Assert.assertEquals(5, cars.size());
        Assert.assertFalse(carListContainsCarWithChassisnumber(cars,"000"));
    }

    @Test
    public void testDeleteCarByWrongChassinumber_shouldNotDeleteAnything() {
        List<Car> cars = carRepo.findAll();
        Assert.assertEquals(6, cars.size());
        Assert.assertFalse(carListContainsCarWithChassisnumber(cars, "XYZ"));

        carRepo.deleteCarByChassisnumber("XYZ");

        cars = carRepo.findAll();
        Assert.assertEquals(6, cars.size());
    }

    @Test
    public void testFindCarByChassinumber_shouldReturnCar() {
        Car car = carRepo.findByChassisnumber("000");
        Assert.assertEquals("000", car.getChassisnumber());
        Assert.assertEquals("Audi", car.getOem());
        Assert.assertEquals("A8", car.getModelType());
    }

    @Test
    public void testFindCarByWrongChassinumber_shouldReturnNoCar() {
        Car car = carRepo.findByChassisnumber("XYZ");
        Assert.assertNull(car);
    }

    @Test
    public void testFindCarByExistingOem_ShouldReturnCars() {
        List<Car> cars = carRepo.findAllByOem("Audi");
        Assert.assertEquals(3, cars.size());

        Assert.assertTrue(carListContainsCarWithChassisnumber(cars,"000"));
        Assert.assertTrue(carListContainsCarWithChassisnumber(cars,"003"));
        Assert.assertTrue(carListContainsCarWithChassisnumber(cars,"004"));
    }

    @Test
    public void testFindCarByNonExistingOem_ShouldReturnNoCars() {
        List<Car> cars = carRepo.findAllByOem("Fantasy");
        Assert.assertTrue(cars.isEmpty());
    }

    @Test
    public void testFindAll_ShouldReturnSixCars() {
        List<Car> cars = carRepo.findAll();
        Assert.assertEquals(6, cars.size());
    }

    @After
    public void tearDown() {
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

    private boolean carListContainsCarWithChassisnumber(List<Car> cars, String chassisnumber) {
        for (Car car: cars) {
            if (chassisnumber.equals(car.getChassisnumber())) {
                return true;
            }
        }
        return false;
    }



}
