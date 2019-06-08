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
import tuwien.dse.eventstorageservice.exception.EventNotFoundException;
import tuwien.dse.eventstorageservice.model.Event;
import tuwien.dse.eventstorageservice.model.Location;
import tuwien.dse.eventstorageservice.persistence.EventRepository;
import tuwien.dse.eventstorageservice.services.EntityStoreRestClient;
import tuwien.dse.eventstorageservice.services.EventNotifyService;
import tuwien.dse.eventstorageservice.services.NotificationStoreRestClient;
import tuwien.dse.eventstorageservice.services.RedisService;

import java.beans.Transient;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RestController
public class EventStorageController {

    private EventRepository repository;
    private EventNotifyService stompService;
    private NotificationStoreRestClient notificationStoreRestClient;
    private EntityStoreRestClient entityStoreRestClient;
    private RedisService redisService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventStorageController.class);

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Autowired
    public void setRepository(EventRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setStompService(EventNotifyService stompService) {
        this.stompService = stompService;
    }

    @Autowired
    public void setNotificationStoreRestClient(NotificationStoreRestClient notificationStoreRestClient) {
        this.notificationStoreRestClient = notificationStoreRestClient;
    }

    @Autowired
    public void setEntityStoreRestClient(EntityStoreRestClient entityStoreRestClient) {
        this.entityStoreRestClient = entityStoreRestClient;
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

    /**
     * Rest-endpoint to save a new event in the database.
     * Position-updates are sent to the Frontend with websockets, so the new position of the car can be displayed in the
     * car view.
     * If the event contains a crash-event, the NotificationStorageService is called and a new Crash is created.
     *
     * @param carEventDto DTO containing all data for the event.
     */
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

        /* send position update to frontend */
        stompService.yell(carEventDto);

        /* check if event is a crash */
        if (carEventDto.getCrashEvent() != null) {
            LOGGER.info("Update for car " + carEventDto.getChassisNumber() + "is a crashevent.");
            try {
                /* create crash in the notificationstorage */
                notificationStoreRestClient.createCrashEvent(event);
            } catch (Exception e) {
                LOGGER.warn("Could not send crash event to notification storage", e);
            }
        }
    }

    /**
     * Rest-endpoint to get all information about a event, by the eventId.
     *
     * @param eventId the Id of the event.
     * @return All event information.
     * @throws EventNotFoundException If no event with the given Id could be found.
     */
    @GetMapping("/eventstorage/events/{eventId}")
    public CarEventDto get(@PathVariable String eventId) throws EventNotFoundException {
        Event event = repository.findById(eventId).orElse(null);
        if (event != null) {
            LOGGER.info("Getting Event with ID " + eventId);
            return convertToCarEventDto(event);
        }
        LOGGER.error("Event with Id " + eventId + "not found");
        throw new EventNotFoundException("Event with Id " + eventId + "not found");
    }

    /**
     * Rest-Event to get events from the database.
     * Events can be filtered by OEM, chassisnumber.
     * The number of the returned events can also be limited.
     * The data saved in the eventStoreage is enriched with data saved in the Entitystorage.
     *
     * @param oem OEM for who's cars events are searched.
     * @param chassisnumber Chassisnumber of the car for which events are searched.
     * @param limit Number of events that should be returned.
     * @return List of Events.
     */
    @GetMapping("/eventstorage/events")
    public List<CarEventDto> getEvents(@RequestParam(required = false) Optional<String> oem, @RequestParam(required = false) Optional<String> chassisnumber, @RequestParam(required = false) Optional<Integer> limit) {

        String url = "/eventstorage/events?";

        /* filter by chassisnumber if requested */
        List<Event> events;
        if (chassisnumber.isPresent()) {
            LOGGER.info("Getting events for car " + chassisnumber);
            events = repository.findAllByChassisnumberOrderByTimestampDesc(chassisnumber.get());
            url += "chassisnumber="+chassisnumber.get();
        } else {
            LOGGER.info("Getting all events");
            events = repository.findAll();
        }

        /* limit number of responses if requested */
        if (limit.isPresent()) {
            events = events.stream().limit(limit.get()).collect(Collectors.toList());
            url += "&limit=" + limit.get();
        }


        /* enrich data with oem information and create CarEventDtos */
        List<CarEventDto> result = events.stream().map(e -> convertToCarEventDto(e)).collect(Collectors.toList());

        /* filter by oem if if requested */
        if (oem.isPresent()) {
            LOGGER.info("Filter events by OEM: " + oem);
            result = result.stream().filter(e -> e.getOem().toLowerCase().equals(oem.get().toLowerCase())).collect(Collectors.toList());
        }

        // Remove null values from failing rest calls
        result = result.stream().filter(e -> e != null).collect(Collectors.toList());

        redisService.cache(url, result);

        return result;
    }

    /**
     * Rest-Endpoint which returns a list of cars with their last logged position within a 3 km radios of the given point.
     *
     * @param lng Longitude of the center of the 3 km circle.
     * @param lat Latitude of the center of the 3 km circle.
     * @return List of cars within the 3 km circle.
     */
    @GetMapping("/eventstorage/events/radius")
    public List<String> getCarsIn3kmRadius(@RequestParam double lng, @RequestParam double lat) {
        LOGGER.info("Getting cars in 3km area of {} {}", lat, lng);
        return repository.findByLocationNearOrderByTimestampDesc(
                new Point(lng, lat),
                new Distance(3, Metrics.KILOMETERS)
        ).stream()
                .map(e -> e.getChassisnumber())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Create a CarEventDto by a Event.
     * Enriches the data saved in the Eventstorage with inromation saved in the Entitystorage.
     * @param event Information for the Event.
     * @return Information for the Event with information for the car.
     */
    private CarEventDto convertToCarEventDto(Event event) {
        CarDto car;
        try {
            car = entityStoreRestClient.getCar(event.getChassisnumber());
        } catch (Exception e) {
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
