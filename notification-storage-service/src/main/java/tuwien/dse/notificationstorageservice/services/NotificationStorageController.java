package tuwien.dse.notificationstorageservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tuwien.dse.notificationstorageservice.dto.CrashDto;

import java.util.Date;

@RestController
public class NotificationStorageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationStorageController.class);

    @Autowired
    private CrashNotifyService stompService;

    @GetMapping("/")
    public String findAll() {
        LOGGER.info("Event find all");
        return "find All events yyyyyyyyyyyyyyyyyyyess";
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
}
