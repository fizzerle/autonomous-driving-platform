package tuwien.dse.eventstorageservice.rest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tuwien.dse.eventstorageservice.dto.CarDto;
import tuwien.dse.eventstorageservice.dto.CarEventDto;
import tuwien.dse.eventstorageservice.exception.EventNotFoundException;
import tuwien.dse.eventstorageservice.model.Event;
import tuwien.dse.eventstorageservice.model.Location;
import tuwien.dse.eventstorageservice.persistence.EventRepository;
import tuwien.dse.eventstorageservice.services.EntityStoreRestClient;
import tuwien.dse.eventstorageservice.services.EventNotifyService;
import tuwien.dse.eventstorageservice.services.NotificationStoreRestClient;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventStorageControllerTest {

    @Autowired
    private EventRepository eventRepo;

    private String eventId1;

    @Before
    public void setup() {
        insertEventTestData();
    }

    @Test
    public void testCreateNoCrash_ShouldCreateEvent() throws Exception {
        EventNotifyService notifyService = mock(EventNotifyService.class);
        NotificationStoreRestClient notificationStoreRestClient = mock(NotificationStoreRestClient.class);

        EventStorageController controller = new EventStorageController();
        controller.setStompService(notifyService);
        controller.setNotificationStoreRestClient(notificationStoreRestClient);
        notifyService.yell(new CarEventDto());
        controller.setRepository(eventRepo);

        doNothing().when(notifyService).yell(any(CarEventDto.class));
        doNothing().when(notificationStoreRestClient).createCrashEvent(any(Event.class));

        int size = eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").size();
        Event event = eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").get(0);

        CarEventDto carEventDto = new CarEventDto("Audi", "001", "A8", 4, new Location(12, 21), 40, 8.0, 4.0, null);
        controller.create(carEventDto);

        Assert.assertEquals(size + 1, eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").size());
        Event newCreated = eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").get(0);

        Assert.assertEquals(21, newCreated.getLocation().getX(), 0.1);
        Assert.assertEquals(12, newCreated.getLocation().getY(), 0.1);

        Assert.assertTrue(newCreated.getTimestamp().after(event.getTimestamp()));
        Assert.assertNull(newCreated.getCrashEvent());
    }

    @Test
    public void testCreateCrash_ShouldCreateEvent() throws Exception {
        EventNotifyService notifyService = mock(EventNotifyService.class);
        NotificationStoreRestClient notificationStoreRestClient = mock(NotificationStoreRestClient.class);

        EventStorageController controller = new EventStorageController();
        controller.setStompService(notifyService);
        controller.setNotificationStoreRestClient(notificationStoreRestClient);
        notifyService.yell(new CarEventDto());
        controller.setRepository(eventRepo);

        doNothing().when(notifyService).yell(any(CarEventDto.class));
        doNothing().when(notificationStoreRestClient).createCrashEvent(any(Event.class));

        int size = eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").size();
        Event event = eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").get(0);

        CarEventDto carEventDto = new CarEventDto("Audi", "001", "A8", 4, new Location(12, 21), 40, 8.0, 4.0, "ran into tree");
        controller.create(carEventDto);

        Assert.assertEquals(size + 1, eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").size());
        Event newCreated = eventRepo.findAllByChassisnumberOrderByTimestampDesc("001").get(0);

        Assert.assertEquals(21, newCreated.getLocation().getX(), 0.1);
        Assert.assertEquals(12, newCreated.getLocation().getY(), 0.1);

        Assert.assertTrue(newCreated.getTimestamp().after(event.getTimestamp()));
        Assert.assertNotNull(newCreated.getCrashEvent());
    }

    @Test
    public void testGetEvent_ShouldReturnCarEventDto() throws EventNotFoundException, Exception {
        EventStorageController controller = new EventStorageController();
        EntityStoreRestClient entityStoreRestClient = mock(EntityStoreRestClient.class);

        CarDto carDto = new CarDto();
        carDto.setOem("Audi");
        carDto.setModelType("A8");
        when(entityStoreRestClient.getCar(anyString())).thenReturn(carDto);

        controller.setRepository(eventRepo);
        controller.setEntityStoreRestClient(entityStoreRestClient);

        CarEventDto dto = controller.get(eventId1);

        Assert.assertEquals(48.210287, dto.getLocation().getLat(), 0.0000001);
        Assert.assertEquals(16.327096, dto.getLocation().getLng(), 0.0000001);

        Assert.assertEquals("001", dto.getChassisNumber());
        Assert.assertEquals(10, dto.getSpeed());
        Assert.assertEquals(0.0, dto.getSpaceAhead(), 0.1);
        Assert.assertEquals(0.0, dto.getSpaceBehind(), 0.1);
        Assert.assertNull(dto.getCrashEvent());
        Assert.assertEquals(4, dto.getPassengers());
    }

    @Test(expected = EventNotFoundException.class)
    public void testGetEventInvalid_ShouldThrowEventNotFoundException() throws EventNotFoundException {
        EventStorageController controller = new EventStorageController();
        controller.setRepository(eventRepo);

        controller.get("");
    }

    @Test
    public void testGetAllEvents_ShouldReturnAllEvents() throws Exception {
        EventStorageController controller = new EventStorageController();
        EntityStoreRestClient entityStoreRestClient = mock(EntityStoreRestClient.class);

        CarDto carDto = new CarDto();
        carDto.setOem("OEM");
        carDto.setModelType("Model");
        when(entityStoreRestClient.getCar(anyString())).thenReturn(carDto);

        controller.setRepository(eventRepo);
        controller.setEntityStoreRestClient(entityStoreRestClient);

        List<CarEventDto> events = controller.getEvents(Optional.empty(), Optional.empty(), Optional.empty());
        Assert.assertEquals(9, events.size());
    }

    @Test
    public void testGetEventsFilterOem_ShouldReturnEvents() throws Exception {
        EventStorageController controller = new EventStorageController();
        EntityStoreRestClient entityStoreRestClient = mock(EntityStoreRestClient.class);

        CarDto carDto = new CarDto();
        carDto.setOem("Audi");
        carDto.setModelType("Model");

        CarDto carDto2 = new CarDto();
        carDto2.setOem("Opel");
        carDto2.setModelType("Model");

        when(entityStoreRestClient.getCar(anyString())).thenReturn(carDto, carDto, carDto, carDto, carDto2);

        controller.setRepository(eventRepo);
        controller.setEntityStoreRestClient(entityStoreRestClient);

        List<CarEventDto> events = controller.getEvents(Optional.of("Audi"), Optional.empty(), Optional.empty());
        Assert.assertEquals(4, events.size());
    }

    @Test
    public void testGetEventsFilterChassisNumber_ShouldReturnEvents() throws Exception {
        EventStorageController controller = new EventStorageController();
        EntityStoreRestClient entityStoreRestClient = mock(EntityStoreRestClient.class);

        CarDto carDto = new CarDto();
        carDto.setOem("Audi");
        carDto.setModelType("Model");

        when(entityStoreRestClient.getCar(anyString())).thenReturn(carDto);

        controller.setRepository(eventRepo);
        controller.setEntityStoreRestClient(entityStoreRestClient);

        List<CarEventDto> events = controller.getEvents(Optional.empty(), Optional.of("001"), Optional.empty());
        Assert.assertEquals(4, events.size());
    }

    @Test
    public void testGetEventsFilterChassisNumberAndLimit_ShouldReturnEvents() throws Exception {
        EventStorageController controller = new EventStorageController();
        EntityStoreRestClient entityStoreRestClient = mock(EntityStoreRestClient.class);

        CarDto carDto = new CarDto();
        carDto.setOem("Audi");
        carDto.setModelType("Model");

        when(entityStoreRestClient.getCar(anyString())).thenReturn(carDto);

        controller.setRepository(eventRepo);
        controller.setEntityStoreRestClient(entityStoreRestClient);

        List<CarEventDto> events = controller.getEvents(Optional.empty(), Optional.of("001"), Optional.of(2));
        Assert.assertEquals(2, events.size());
    }

    @Test
    public void testGetCarsIn3kmRadius_shouldReturnCars() {
        EventStorageController controller = new EventStorageController();
        controller.setRepository(eventRepo);

        List<String> cars = controller.getCarsIn3kmRadius(16.3, 48.2);
        Assert.assertEquals(2, cars.size());
        Assert.assertTrue(cars.contains("001"));
        Assert.assertTrue(cars.contains("002"));
        Assert.assertFalse(cars.contains("003"));

        cars = controller.getCarsIn3kmRadius(1, 1);
        Assert.assertEquals(1, cars.size());
        Assert.assertFalse(cars.contains("001"));
        Assert.assertFalse(cars.contains("002"));
        Assert.assertTrue(cars.contains("003"));
    }

    @Test
    public void testGetCarsIn3kmRadiusChina_shouldReturnNoCars() {
        EventStorageController controller = new EventStorageController();
        controller.setRepository(eventRepo);

        List<String> cars = controller.getCarsIn3kmRadius(114.005179, 34.561383);
        Assert.assertTrue(cars.isEmpty());
    }


    @After
    public void cleanup() {
        eventRepo.deleteAll();
    }

    private void insertEventTestData() {
        eventRepo.deleteAll();
        eventId1 = eventRepo.save(new Event(new Location(48.210287, 16.327096), "001", new Date(), 10, 0.0, 0.0, null, 4)).getId();
        eventRepo.save(new Event(new Location(48.210147, 16.328123), "001", new Date(), 10, 2.0, 2.0, null, 4));
        eventRepo.save(new Event(new Location(48.209968, 16.329310), "001", new Date(), 10, 3.0, 3.0, null, 4));
        eventRepo.save(new Event(new Location(48.209790, 16.330330), "001", new Date(), 10, 0.0, 0.0, "i crashed", 4));
        eventRepo.save(new Event(new Location(48.211630, 16.325520), "002", new Date(), 40, 0.0, 0.0, null, 5));
        eventRepo.save(new Event(new Location(48.211388, 16.327351), "002", new Date(), 40, 9.0, 3.0, null, 5));
        eventRepo.save(new Event(new Location(48.211180, 16.328742), "002", new Date(), 40, 8.0, 4.0, null, 5));
        eventRepo.save(new Event(new Location(1, 1), "003", new Date(), 20, 7.0, 5.0, null, 2));
        eventRepo.save(new Event(new Location(2, 2), "003", new Date(), 20, 6.0, 9.0, null, 2));
    }
}
