package tuwien.dse.eventstorageservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import tuwien.dse.eventstorageservice.dto.CarDataDto;
import tuwien.dse.eventstorageservice.model.Event;
import tuwien.dse.eventstorageservice.model.Location;
import tuwien.dse.eventstorageservice.persistence.EventRepository;

import java.util.Date;
import java.util.List;

@Component
@RestController
public class EventStorageController {

    @Autowired
    private EventRepository repository;

    @Autowired
    private EventNotifyService stompService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventStorageController.class);

    @GetMapping("/")
    public List<Event> findAll() {
        LOGGER.info("Event find all");
        return repository.findAll();
        //return "find All events yyyyyyyyyyyyyyyyyyyess";
    }

    @GetMapping("/test/")
    public String test() {
        LOGGER.info("test called");
        return "test called";
    }

    @GetMapping("/eventstorage/test")
    public String test2() {
        LOGGER.info("eventstorage test called");
        return "eventstorage test";
    }

    @GetMapping("/eventstorage/stomp")
    public String stompTest() {
        LOGGER.info("stomptest test called");
        CarDataDto data = new CarDataDto();
        data.setOem("Audi");
        data.setChassisNumber("B567GK");
        data.setLocation(new Location(45, 48));
        data.setModeltype("A8");
        data.setPassengers(1);
        data.setSpaceAhead(50);
        data.setSpaceBehind(30);
        data.setSpeed(30);
        stompService.yell(data);
        return "stomp request sent";
    }

    @PostMapping("/carData")
    public Event create(@RequestBody CarDataDto carDataDto) {
        Event event = new Event(carDataDto.getLocation(),carDataDto.getChassisNumber(), new Date(), carDataDto.getSpeed(), carDataDto.getSpaceAhead(), carDataDto.getSpaceBehind(), carDataDto.getCrashEvent());

        if (carDataDto.getCrashEvent() != null) {
            //TODO send data to notification service
        }

        return repository.save(event);
    }

    @GetMapping("/{eventId}")
    public Event get(@PathVariable String eventId) {
        return repository.findById(eventId).orElse(null);
    }

    @GetMapping("/events/{chassisnumber}")
    public List<Event> getAllByChassisnumber(@PathVariable String chassisnumber) {
        return repository.findAllByChassisnumber(chassisnumber);
    }

    @GetMapping("/latest/{chassisnumber}")
    public Event getLatestByChassisnumber(@PathVariable String chassisnumber) {
        List<Event> events = repository.findAllByChassisnumber(chassisnumber);
        if (events.isEmpty()) return null;

        Event latest = events.get(0);
        for (Event event: events) {
            if (event.getTimestamp().after(latest.getTimestamp())) {
                latest = event;
            }
        }
        return latest;
    }


}
