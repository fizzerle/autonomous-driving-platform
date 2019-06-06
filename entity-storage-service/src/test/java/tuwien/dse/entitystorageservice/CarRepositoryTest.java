package tuwien.dse.entitystorageservice;


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
    private CarRepository someRepository;

    @Before
    public void init() {
        Car car = new Car();
        car.setChassisnumber("012");
        car.setModelType("AMG");
        car.setOem("Mercedes");
        someRepository.save(car);
    }

    @Test
    public void someTest() {
        List<Car> cars = someRepository.findAll();

        Assert.assertEquals(1, cars.size());

        // assertions
    }

}
