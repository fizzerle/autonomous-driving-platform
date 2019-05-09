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
import tuwien.dse.notificationstorageservice.services.BlueLightOrganisationService;
import tuwien.dse.notificationstorageservice.services.CrashNotifyService;
import tuwien.dse.notificationstorageservice.services.OemNotificaionService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class NotificationStorageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationStorageController.class);

    @Autowired
    private CrashNotifyService stompService;

    @Autowired
    CrashRepository crashRepository;
    @Autowired
    OemNotificaionService oemNotificaionService;
    @Autowired
    BlueLightOrganisationService blueLightOrganisationService;

    @GetMapping("/test")
    public String test() {
        LOGGER.info("test called");
        return "test called";
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

    @GetMapping("/notifications")
    public List<?> getNotificationsForOem(@RequestParam(required = false) Optional<String> oem,
                                                           @RequestParam(defaultValue = "false") boolean active) {
        if (oem.isPresent()) {
            return oemNotificaionService.getOemNotifications(oem.get());
        }
        if (active) {
            return blueLightOrganisationService.getAllActiveAccidents();
        }
        return blueLightOrganisationService.getAllAccidents();
    }

    @PostMapping("/notifications")
    public void createCrashEvent(@RequestBody CrashEventDto crashEventDto) {
        CrashEvent event = new CrashEvent();
        event.setChassisnumber(crashEventDto.getChassisNumber());
        event.setCrashTimestamp(crashEventDto.getTimestamp());
        event.setDescription(crashEventDto.getDescription());
        event.setEventId(crashEventDto.getEventId());

        event = crashRepository.save(event);
        stompService.yell(event);
    }

    @PatchMapping("/notifications/{crashId}")
    public void resolveCrashEvent(@PathVariable String crashId) throws CrashNotFoundException, CrashAlreadyInactiveException {
        CrashEvent event = crashRepository.findById(crashId).orElse(null);
        if (event == null) throw new CrashNotFoundException("crash with Id" + crashId + "not found");
        if (event.getResolveTimestamp() != null) throw new CrashAlreadyInactiveException("Crash with Id " + crashId + "is already inactive");

        event.setResolveTimestamp(new Date());
        event = crashRepository.save(event);
        stompService.yell(event);
    }
}
