package tuwien.dse.notificationstorageservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tuwien.dse.notificationstorageservice.dto.CrashDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tuwien.dse.notificationstorageservice.dto.BlueLightOrgNotificationDto;
import tuwien.dse.notificationstorageservice.dto.CrashEventDto;
import tuwien.dse.notificationstorageservice.dto.OemNotificationDto;
import tuwien.dse.notificationstorageservice.exception.CrashAlreadyInactiveException;
import tuwien.dse.notificationstorageservice.exception.CrashNotFoundException;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.Date;
import java.util.List;

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

    @GetMapping("/")
    public List<CrashEvent> findAll() {
        LOGGER.info("Event find all");
        return crashRepository.findAll();
    }

    @GetMapping("/test/")
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
        CrashDto data = new CrashDto();
        data.setLocation("40.751444, -73.956990");
        data.setActive(true);
        data.setChassis("B567GK");
        data.setModel("A8");
        data.setOem("Audi");
        data.setOccupants(1);
        data.setTimestamp(new Date());
        stompService.yell(data);
        return "crash reported";
    }

    @GetMapping("/notificationstorage/stomp/inactive")
    public String stompResolveCrash() {
        LOGGER.info("crash resolved");
        CrashDto data = new CrashDto();
        data.setLocation("40.751444, -73.956990");
        data.setActive(false);
        data.setChassis("B567GK");
        data.setModel("A8");
        data.setOem("Audi");
        data.setOccupants(1);
        data.setTimestamp(new Date());
        stompService.yell(data);
        return "crash resolved";
    }

    @GetMapping("/oemNotifications/{oem}")
    public List<OemNotificationDto> getNotificationsForOem(@PathVariable String oem) {
        return oemNotificaionService.getOemNotifications(oem);
    }

    @GetMapping("/bluelight/active/")
    public List<BlueLightOrgNotificationDto> getAllActiveAccidents() {
        return blueLightOrganisationService.getAllActiveAccidents();
    }

    @GetMapping("/bluelight/all/")
    public List<BlueLightOrgNotificationDto> getAllAccidents() {
        return blueLightOrganisationService.getAllAccidents();
    }

    @PostMapping("/crashEvent/")
    public CrashEvent createCrashEvent(@RequestBody CrashEventDto crashEventDto) {
        CrashEvent event = new CrashEvent();
        event.setChassisnumber(crashEventDto.getChassisNumber());
        event.setCrashTimestamp(crashEventDto.getTimestamp());
        event.setDescription(crashEventDto.getDescription());
        event.setEventId(crashEventDto.getEventId());
        return crashRepository.save(event);
    }

    @PostMapping("/setInactive/{crashId}")
    public CrashEvent setCrashToInactive(@PathVariable String crashId) throws CrashNotFoundException, CrashAlreadyInactiveException {
        CrashEvent event = crashRepository.findById(crashId).orElse(null);
        if (event == null) throw new CrashNotFoundException("crash with Id" + crashId + "not found");
        if (event.getSetInactiveTimestamp() != null) throw new CrashAlreadyInactiveException("Crash with Id " + crashId + "is already inactive");

        event.setSetInactiveTimestamp(new Date());
        return crashRepository.save(event);
    }
}
