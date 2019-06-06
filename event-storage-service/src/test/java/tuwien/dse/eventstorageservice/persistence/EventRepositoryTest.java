package tuwien.dse.eventstorageservice.persistence;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.context.junit4.SpringRunner;
import tuwien.dse.eventstorageservice.model.Event;
import tuwien.dse.eventstorageservice.model.Location;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventRepositoryTest {

    private String eventId1;

    @Autowired
    private EventRepository eventRepo;

    @Before
    public void setup() {
        insertEventTestData();
    }

    @Test
    public void testFindAll_ShouldReturnNineResults() {
        List<Event> events = eventRepo.findAll();
        Assert.assertEquals(9, events.size());
    }

    @Test
    public void testFindAllByChassisnumberOrderByTimestampDesc_ShouldReturnEventsInRightOrder() {
        List<Event> events = eventRepo.findAllByChassisnumberOrderByTimestampDesc("001");
        Assert.assertEquals(4, events.size());

        Date date = new Date();
        for (Event event: events) {
            Assert.assertTrue(event.getTimestamp().before(date));
            date = event.getTimestamp();
        }
    }

    @Test
    public void testFindAllByChassisnumberOrderByTimestampDescWrongChassisnumber_ShouldReturnNoEvents() {
        List<Event> events = eventRepo.findAllByChassisnumberOrderByTimestampDesc("XYZ");
        Assert.assertTrue(events.isEmpty());
    }

    @Test
    public void testFindById_ShouldReturnEvent() {
        Event event = eventRepo.findById(eventId1).get();

        GeoJsonPoint location = event.getLocation();

        Assert.assertEquals(48.210287, location.getY(), 0.0000001);
        Assert.assertEquals(16.327096, location.getX(), 0.0000001);

        Assert.assertEquals("001", event.getChassisnumber());
        Assert.assertEquals(10, event.getSpeed());
        Assert.assertEquals(0.0, event.getSpaceAhead(), 0.1);
        Assert.assertEquals(0.0, event.getSpaceBehind(), 0.1);
        Assert.assertNull(event.getCrashEvent());
        Assert.assertEquals(4,event.getPassengers());
    }

    @Test
    public void testFindByIdWithInvalidId_ShouldReturnNoEvent() {
        Event event = eventRepo.findById("").orElse(null);
        Assert.assertNull(event);
    }

    @Test
    public void testFindByLocationNearOrderByTimestampDesc_ShouldReturnNotAllEvents() {
        List<Event> events = eventRepo.findByLocationNearOrderByTimestampDesc(new Point(16.3,48.2),new Distance(3, Metrics.KILOMETERS));

        Assert.assertEquals(7,events.size());

        events = eventRepo.findByLocationNearOrderByTimestampDesc(new Point(1,1),new Distance(3, Metrics.KILOMETERS));

        Assert.assertEquals(1,events.size());
    }

    @Test
    public void testFindByLocationNearOrderByTimestampDescInChina_ShouldReturnNoResults() {
        List<Event> events = eventRepo.findByLocationNearOrderByTimestampDesc(new Point(114.005179,34.561383),new Distance(3, Metrics.KILOMETERS));

        Assert.assertTrue(events.isEmpty());
    }



    @After
    public void tearDown() {
        eventRepo.deleteAll();
    }

    private void insertEventTestData() {
        eventRepo.deleteAll();
        eventId1 = eventRepo.save(new Event(new Location(48.210287,16.327096),"001",new Date(),10,0.0,0.0,null,4)).getId();
        eventRepo.save(new Event(new Location(48.210147,16.328123),"001",new Date(),10,2.0,2.0,null,4));
        eventRepo.save(new Event(new Location(48.209968,16.329310),"001",new Date(),10,3.0,3.0,null,4));
        eventRepo.save(new Event(new Location(48.209790,16.330330),"001",new Date(),10,0.0,0.0,"i crashed",4));
        eventRepo.save(new Event(new Location(48.211630,16.325520),"002",new Date(),40,0.0,0.0,null,5));
        eventRepo.save(new Event(new Location(48.211388,16.327351),"002",new Date(),40,9.0,3.0,null,5));
        eventRepo.save(new Event(new Location(48.211180,16.328742),"002",new Date(),40,8.0,4.0,null,5));
        eventRepo.save(new Event(new Location(1,1),"003",new Date(),20,7.0,5.0,null,2));
        eventRepo.save(new Event(new Location(2,2),"003",new Date(),20,6.0,9.0,null,2));
    }



}
