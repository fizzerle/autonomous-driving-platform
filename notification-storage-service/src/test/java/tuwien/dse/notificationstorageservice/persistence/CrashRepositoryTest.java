package tuwien.dse.notificationstorageservice.persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tuwien.dse.notificationstorageservice.model.CrashEvent;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrashRepositoryTest {

    @Autowired
    private CrashRepository crashRepo;

    @Before
    public void setup() {
        crashRepo.deleteAll();
        crashRepo.save(new CrashEvent("crash1", "001", new Date(), new Date(), "ran into tree"));
        crashRepo.save(new CrashEvent("crash2", "002", new Date(), new Date(), "ran into deer"));
    }

    @Test
    public void testFindAll_ShouldReturnAllCrashes() {
        List<CrashEvent> crashes = crashRepo.findAll();
        Assert.assertEquals(2, crashes.size());
    }

    @After
    public void cleanup() {
        crashRepo.deleteAll();
    }

}
