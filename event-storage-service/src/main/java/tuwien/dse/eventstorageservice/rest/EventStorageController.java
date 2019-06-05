package tuwien.dse.eventstorageservice.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
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

    @DeleteMapping("/eventstorage/clean")
    public String clean() {
        repository.deleteAll();
        return "deleted all events";
    }

    @PostMapping("/events")
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

    @GetMapping("/events/{eventId}")
    public CarEventDto get(@PathVariable String eventId) {
        Event event = repository.findById(eventId).orElse(null);
        if (event != null) {

        }
        return convertToCarEventDto(event);
    }

    @GetMapping("/events")
    public List<CarEventDto> getEvents(@RequestParam(required = false) Optional<String> oem, @RequestParam(required = false) Optional<String> chassisnumber, @RequestParam(required = false) Optional<Integer> limit) {
        List<Event> events;
        if (chassisnumber.isPresent()) {
            events = repository.findAllByChassisnumberOrderByTimestampDesc(chassisnumber.get());
        } else {
            events = repository.findAll();//OrOrderByTimestampDesc();
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

    @GetMapping("/events/radius")
    public List<String> getCarsIn3kmRadius(@RequestParam double lng, @RequestParam double lat) {
        return repository.findByLocationNearOrderByTimestampDesc(
                new Point(lng, lat),
                new Distance(3, Metrics.KILOMETERS)
        ).stream()
                .map(e -> e.getChassisnumber())
                .distinct()
                .collect(Collectors.toList());
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
                new Location(event.getLocation().getY(), event.getLocation().getX()),
                event.getSpeed(),
                event.getSpaceAhead(),
                event.getSpaceBehind(),
                event.getCrashEvent()
        );
    }

}
