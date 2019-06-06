package tuwien.dse.entitystorageservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EntitystorageserviceApplication.class)
@TestPropertySource(locations = "classpath:application-dev.properties")
public class EntitystorageserviceApplicationTests {

    @Test
    public void contextLoads() {
    }

}
