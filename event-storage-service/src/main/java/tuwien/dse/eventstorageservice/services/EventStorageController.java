package tuwien.dse.eventstorageservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tuwien.dse.eventstorageservice.dto.CarDataDto;
import tuwien.dse.eventstorageservice.model.Event;
import tuwien.dse.eventstorageservice.persistence.EventRepository;

import java.util.Date;
import java.util.List;

@Component
@RestController
public class EventStorageController {

    @Autowired
    private EventRepository repository;

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

    @PostMapping("/carData")
    public Event create(@RequestBody CarDataDto carDataDto) {
        Event event = new Event(carDataDto.getLocation(),carDataDto.getChassisNumber(), new Date(), carDataDto.getSpeed(), carDataDto.getSpaceAhead(), carDataDto.getSpaceBehind(), carDataDto.getCrashEvent());

        if (carDataDto.getCrashEvent() != null) {
            //TODO send data to notification service
        }

        return repository.save(event);
    }

    @GetMapping("/{eventId}")
    public Event get(@RequestBody String eventId) {
        return repository.findById(eventId).orElse(null);
    }


}
