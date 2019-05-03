package tuwien.dse.notificationstorageservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationStorageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationStorageController.class);

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
}
