package tuwien.dse.notificationstorageservice.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
import tuwien.dse.notificationstorageservice.dto.BlueLightOrgNotificationDto;
import tuwien.dse.notificationstorageservice.dto.CarNotificationDto;
import tuwien.dse.notificationstorageservice.dto.CrashEventDto;
import tuwien.dse.notificationstorageservice.dto.Location;
import tuwien.dse.notificationstorageservice.dto.OemNotificationDto;
import tuwien.dse.notificationstorageservice.exception.CrashAlreadyInactiveException;
import tuwien.dse.notificationstorageservice.exception.CrashNotFoundException;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;
import tuwien.dse.notificationstorageservice.services.AutonomousCarService;
import tuwien.dse.notificationstorageservice.services.BlueLightOrganisationService;
import tuwien.dse.notificationstorageservice.services.CrashNotifyService;
import tuwien.dse.notificationstorageservice.services.OemNotificaionService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController("test")
public class NotificationStorageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationStorageController.class);
    private static final String CLIENT_TYPE_CAR = "Car";
    private static final String CLIENT_TYPE_OEM = "OEM";
    private static final String CLIENT_TYPE_BLUELIGHT = "Bluelight";

    @Autowired
    private CrashNotifyService stompService;

    @Autowired
    CrashRepository crashRepository;
    @Autowired
    OemNotificaionService oemNotificaionService;
    @Autowired
    BlueLightOrganisationService blueLightOrganisationService;

    @Autowired
    private AutonomousCarService carService;

    @GetMapping("/test")
    public String test() {
        LOGGER.info("test called");
        return "test called notificationservice";
    }

    @GetMapping("/notificationstorage/test")
    public String test2() {
        LOGGER.info("notificationstorage test called");
        return "notificationstorage test";
    }

    @GetMapping("/notificationstorage/stomp/active")
    public String stompCreateCrash() {
        LOGGER.info("crash reported");

        BlueLightOrgNotificationDto bl = new BlueLightOrgNotificationDto();
        bl.setChassisnumber("B567GK");
        bl.setCrashId("1");
        bl.setLocation(new Location(40.751444, -73.956990));
        bl.setOem("Audi");
        bl.setModeltype("A8");
        bl.setPassengers(1);
        bl.setTimestamp(new Date());
        bl.setResolveTimestamp(null);

        OemNotificationDto oem = new OemNotificationDto();
        oem.setChassisnumber(bl.getChassisnumber());
        oem.setLocation(bl.getLocation());
        oem.setTimestamp(bl.getTimestamp());
        oem.setDescription("Unicorn");
        oem.setResolveTimestamp(null);

        CarNotificationDto car = new CarNotificationDto();
        car.setActive(true);
        car.setLocation(bl.getLocation());

        stompService.notifyBluelights(bl);
        stompService.notifyCars(car);
        stompService.notifyOem(bl.getOem(), oem);

        return "crash reported";
    }

    @DeleteMapping("/notificationstorage/clean")
    public String clean() {
        crashRepository.deleteAll();
        return "deleted all notifications";
    }

    @GetMapping("/notificationstorage/stomp/inactive")
    public String stompResolveCrash() {
        LOGGER.info("crash resolved");

        BlueLightOrgNotificationDto bl = new BlueLightOrgNotificationDto();
        bl.setChassisnumber("B567GK");
        bl.setCrashId("1");
        bl.setLocation(new Location(40.751444, -73.956990));
        bl.setOem("Audi");
        bl.setModeltype("A8");
        bl.setPassengers(1);
        bl.setTimestamp(new Date());
        bl.setResolveTimestamp(new Date());

        OemNotificationDto oem = new OemNotificationDto();
        oem.setChassisnumber(bl.getChassisnumber());
        oem.setLocation(bl.getLocation());
        oem.setTimestamp(bl.getTimestamp());
        oem.setDescription("Unicorn");
        oem.setResolveTimestamp(new Date());

        CarNotificationDto car = new CarNotificationDto();
        car.setActive(false);
        car.setLocation(bl.getLocation());

        stompService.notifyBluelights(bl);
        stompService.notifyCars(car);
        stompService.notifyOem(bl.getOem(), oem);

        return "crash resolved";
    }

    @GetMapping("/notificationstorage/notifications")
    public List<?> getCrashEvents(@RequestHeader(value="X-Client-Type") String clientType,
                                  @RequestParam(required = false) Optional<String> oem,
                                  @RequestParam(defaultValue = "false") boolean active) {
        if (CLIENT_TYPE_CAR.equals(clientType)) {
            // Get all active crashes
            return carService.getAllActiveCrashEvents();
        } else if (CLIENT_TYPE_OEM.equals(clientType)) {
            // Get all crashes by oem
            if (oem.isPresent()) {
                return oemNotificaionService.getOemNotifications(oem.get());
            }
            // Throw missing parameter exception
            return null;
        } else if (CLIENT_TYPE_BLUELIGHT.equals(clientType)) {
            // Get all crashes
            return blueLightOrganisationService.getAllAccidents();
        } else {
            // Throw missing header exception
            return null;
        }
    }

    @PostMapping("/notificationstorage/notifications")
    public void createCrashEvent(@RequestBody CrashEventDto crashEventDto) {
        LOGGER.info("Creating new Crash with chassis {}", crashEventDto.getChassisNumber());
        CrashEvent event = new CrashEvent();
        event.setChassisnumber(crashEventDto.getChassisNumber());
        event.setCrashTimestamp(crashEventDto.getTimestamp());
        event.setDescription(crashEventDto.getDescription());
        event.setEventId(crashEventDto.getEventId());

        event = crashRepository.save(event);
        stompService.yell(event);
    }

    @PatchMapping("/notificationstorage/notifications/{crashId}")
    public void resolveCrashEvent(@PathVariable String crashId) throws CrashNotFoundException, CrashAlreadyInactiveException {
        LOGGER.info("Resolving Crash with id {}", crashId);
        CrashEvent event = crashRepository.findById(crashId).orElse(null);
        if (event == null) throw new CrashNotFoundException("crash with Id" + crashId + "not found");
        if (event.getResolveTimestamp() != null) throw new CrashAlreadyInactiveException("Crash with Id " + crashId + "is already inactive");

        event.setResolveTimestamp(new Date());
        event = crashRepository.save(event);
        stompService.yell(event);
    }
}
