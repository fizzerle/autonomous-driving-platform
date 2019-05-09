package tuwien.dse.eventstorageservice.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import tuwien.dse.eventstorageservice.dto.CarDto;
import tuwien.dse.eventstorageservice.dto.CarEventDto;
import tuwien.dse.eventstorageservice.model.Event;
import tuwien.dse.eventstorageservice.model.Location;
import tuwien.dse.eventstorageservice.persistence.EventRepository;
import tuwien.dse.eventstorageservice.services.EntityStoreRestClient;
import tuwien.dse.eventstorageservice.services.EventNotifyService;
import tuwien.dse.eventstorageservice.services.NotificationStoreRestClient;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RestController
public class EventStorageController {

    @Autowired
    private EventRepository repository;

    @Autowired
    private EventNotifyService stompService;

    @Autowired
    private NotificationStoreRestClient notificationStoreRestClient;

    @Autowired
    private EntityStoreRestClient entityStoreRestClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventStorageController.class);

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
        CarEventDto data = new CarEventDto();
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

    @PostMapping("/eventstorage/events")
    public void create(@RequestBody CarEventDto carEventDto) {
        LOGGER.info("Update from car with chassis {}", carEventDto.getChassisNumber());
        Event event = new Event(
                carEventDto.getLocation(),
                carEventDto.getChassisNumber(),
                new Date(),
                carEventDto.getSpeed(),
                carEventDto.getSpaceAhead(),
                carEventDto.getSpaceBehind(),
                carEventDto.getCrashEvent(),
                carEventDto.getPassengers()
        );
        event = repository.save(event);

        stompService.yell(carEventDto);

        if (carEventDto.getCrashEvent() != null) {
            try {
                notificationStoreRestClient.createCrashEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/eventstorage/events/{eventId}")
    public CarEventDto get(@PathVariable String eventId) {
        Event event = repository.findById(eventId).orElse(null);
        if (event != null) {

        }
        return convertToCarEventDto(event);
    }

    @GetMapping("/eventstorage/events")
    public List<CarEventDto> getEvents(@RequestParam(required = false) Optional<String> oem, @RequestParam(required = false) Optional<String> chassisnumber, @RequestParam(required = false) Optional<Integer> limit) {
        List<Event> events;
        if (chassisnumber.isPresent()) {
            events = repository.findAllByChassisnumber(chassisnumber.get());
        } else {
            events = repository.findAll();
        }

        if (limit.isPresent()) {
            events = events.stream().limit(limit.get()).collect(Collectors.toList());
        }
        List<CarEventDto> result = events.stream().map(e -> convertToCarEventDto(e)).collect(Collectors.toList());
        if (oem.isPresent()) {
            result = result.stream().filter(e -> e.getOem().toLowerCase().equals(oem.get().toLowerCase())).collect(Collectors.toList());
        }
        return result;
    }

    private CarEventDto convertToCarEventDto(Event event) {
        CarDto car;
        try {
            car = entityStoreRestClient.getCar(event.getChassisnumber());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new CarEventDto(
                car.getOem(),
                event.getChassisnumber(),
                car.getModelType(),
                event.getPassengers(),
                event.getLocation(),
                event.getSpeed(),
                event.getSpaceAhead(),
                event.getSpaceBehind(),
                event.getCrashEvent()
        );
    }

}
