package tuwien.dse.notificationstorageservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = NotificationstorageserviceApplication.class)
@TestPropertySource(locations = "classpath:application-dev.properties")
public class NotificationstorageserviceApplicationTests {

    @Test
    public void contextLoads() {
    }

}
