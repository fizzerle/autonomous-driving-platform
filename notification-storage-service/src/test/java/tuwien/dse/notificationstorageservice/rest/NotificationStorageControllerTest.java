package tuwien.dse.notificationstorageservice.rest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import tuwien.dse.notificationstorageservice.NotificationstorageserviceApplication;
import tuwien.dse.notificationstorageservice.dto.*;
import tuwien.dse.notificationstorageservice.exception.BadRequestException;
import tuwien.dse.notificationstorageservice.exception.CrashAlreadyInactiveException;
import tuwien.dse.notificationstorageservice.exception.CrashNotFoundException;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;
import tuwien.dse.notificationstorageservice.services.AutonomousCarService;
import tuwien.dse.notificationstorageservice.services.BlueLightOrganisationService;
import tuwien.dse.notificationstorageservice.services.CrashNotifyService;
import tuwien.dse.notificationstorageservice.services.OemNotificaionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = NotificationstorageserviceApplication.class)
@TestPropertySource(locations = "classpath:application-dev.properties")
public class NotificationStorageControllerTest {


    @Autowired
    private CrashRepository crashRepo;

    private String crashId1;
    private String crashId2;

    @Before
    public void setup() {
        crashRepo.deleteAll();
        crashId1 = crashRepo.save(new CrashEvent("crash1", "001", new Date(), new Date(), "ran into tree")).getCrashId();
        crashId2 = crashRepo.save(new CrashEvent("crash2", "002", new Date(), null, "ran into deer")).getCrashId();
    }

    @Test
    public void testGetCrashEventsForCar_ShouldReturnCrashEvents() throws BadRequestException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        AutonomousCarService carService = mock(AutonomousCarService.class);

        List<CarNotificationDto> notifications = new ArrayList<>();
        notifications.add(new CarNotificationDto(new Location(1, 2), false));
        notifications.add(new CarNotificationDto(new Location(2, 3), true));
        when(carService.getAllActiveCrashEvents()).thenReturn(notifications);

        notificationStorageController.setCarService(carService);

        List<CarNotificationDto> returnedNotifications = (List<CarNotificationDto>) notificationStorageController.getCrashEvents("Car", Optional.empty());

        Assert.assertEquals(notifications, returnedNotifications);
    }

    @Test
    public void testGetCrashEventsForOem_ShouldReturnCrashEvents() throws BadRequestException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        OemNotificaionService oemNotificaionService = mock(OemNotificaionService.class);

        List<OemNotificationDto> notifications = new ArrayList<>();
        notifications.add(new OemNotificationDto("crash1", new Date(), "ran into tree", "001", null, new Location(1, 2)));
        notifications.add(new OemNotificationDto("crash2", new Date(), "ran into deer", "002", new Date(), new Location(1, 2)));
        when(oemNotificaionService.getOemNotifications(anyString())).thenReturn(notifications);

        notificationStorageController.setOemNotificaionService(oemNotificaionService);

        List<CarNotificationDto> returnedNotifications = (List<CarNotificationDto>) notificationStorageController.getCrashEvents("OEM", Optional.of("Audi"));

        Assert.assertEquals(notifications, returnedNotifications);
    }

    @Test(expected = BadRequestException.class)
    public void testGetCrashEventsWithoutOemArgument_ShouldThrowBadRequestException() throws BadRequestException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        notificationStorageController.getCrashEvents("OEM", Optional.empty());
    }

    @Test
    public void testGetCrashEventsBluelight_ShouldReturnCrashEvents() throws BadRequestException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        BlueLightOrganisationService blueLightOrganisationService = mock(BlueLightOrganisationService.class);

        List<BlueLightOrgNotificationDto> notifications = new ArrayList<>();
        notifications.add(new BlueLightOrgNotificationDto("crash1", "Audi", "001", "A8", new Location(1, 2), new Date(), new Date(), 4));
        notifications.add(new BlueLightOrgNotificationDto("crash2", "Audi", "002", "A3", new Location(2, 1), null, new Date(), 4));
        when(blueLightOrganisationService.getAllAccidents()).thenReturn(notifications);

        notificationStorageController.setBlueLightOrganisationService(blueLightOrganisationService);

        List<BlueLightOrgNotificationDto> returnedNotifications = (List<BlueLightOrgNotificationDto>) notificationStorageController.getCrashEvents("Bluelight", Optional.of("Audi"));

        Assert.assertEquals(notifications, returnedNotifications);
    }

    @Test(expected = BadRequestException.class)
    public void testGetCrashEventsWithInvalidClientTypeHeader_ShouldThowBadRequestException() throws BadRequestException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.getCrashEvents("madeUpHeader", Optional.empty());
    }

    @Test
    public void createCrashEvent_ShouldPersistCrashEvent() {
        CrashEventDto crashEventDto = new CrashEventDto("123", "event1", new Date(), "i am a test crashEvent");

        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        CrashNotifyService stompService = mock(CrashNotifyService.class);
        doNothing().when(stompService).yell(any(CrashEvent.class));

        notificationStorageController.setStompService(stompService);

        Assert.assertEquals(2, crashRepo.findAll().size());

        notificationStorageController.createCrashEvent(crashEventDto);

        Assert.assertEquals(3, crashRepo.findAll().size());
    }

    @Test
    public void testResolveCrashEvent_ShouldResolveCrash() throws CrashAlreadyInactiveException, CrashNotFoundException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        CrashNotifyService stompService = mock(CrashNotifyService.class);
        doNothing().when(stompService).yell(any(CrashEvent.class));
        notificationStorageController.setStompService(stompService);

        CrashEvent event = crashRepo.findById(crashId2).get();
        Assert.assertNull(event.getResolveTimestamp());
        notificationStorageController.resolveCrashEvent(crashId2);
        event = crashRepo.findById(crashId2).get();
        Assert.assertNotNull(event.getResolveTimestamp());
    }

    @Test(expected = CrashAlreadyInactiveException.class)
    public void testResolveCrashEventAlreadyResolved_ShouldThrowCrashAlreadyInactiveException() throws CrashAlreadyInactiveException, CrashNotFoundException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        List<CrashEvent> crashes = crashRepo.findAll();

        notificationStorageController.resolveCrashEvent(crashId1);
    }

    @Test(expected = CrashNotFoundException.class)
    public void testResolveCrashEventInvalidId_ShouldThrowCrashNotFoundException() throws CrashAlreadyInactiveException, CrashNotFoundException {
        NotificationStorageController notificationStorageController = new NotificationStorageController();
        notificationStorageController.setCrashRepository(crashRepo);

        notificationStorageController.resolveCrashEvent("madeUpId");
    }

    @After
    public void cleanup() {
        crashRepo.deleteAll();
    }


}
