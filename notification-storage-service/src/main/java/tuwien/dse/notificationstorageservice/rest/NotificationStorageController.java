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
import tuwien.dse.notificationstorageservice.exception.BadRequestException;
import tuwien.dse.notificationstorageservice.exception.CrashAlreadyInactiveException;
import tuwien.dse.notificationstorageservice.exception.CrashNotFoundException;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;
import tuwien.dse.notificationstorageservice.services.AutonomousCarService;
import tuwien.dse.notificationstorageservice.services.BlueLightOrganisationService;
import tuwien.dse.notificationstorageservice.services.CrashNotifyService;
import tuwien.dse.notificationstorageservice.services.OemNotificaionService;
import tuwien.dse.notificationstorageservice.services.RedisService;

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


    private CrashNotifyService stompService;

    private CrashRepository crashRepository;
    private OemNotificaionService oemNotificaionService;
    private BlueLightOrganisationService blueLightOrganisationService;
    private AutonomousCarService carService;
    private RedisService redisService;

    @Autowired
    public void setStompService(CrashNotifyService stompService) {
        this.stompService = stompService;
    }

    @Autowired
    public void setCrashRepository(CrashRepository crashRepository) {
        this.crashRepository = crashRepository;
    }

    @Autowired
    public void setOemNotificaionService(OemNotificaionService oemNotificaionService) {
        this.oemNotificaionService = oemNotificaionService;
    }

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Autowired
    public void setBlueLightOrganisationService(BlueLightOrganisationService blueLightOrganisationService) {
        this.blueLightOrganisationService = blueLightOrganisationService;
    }

    @Autowired
    public void setCarService(AutonomousCarService carService) {
        this.carService = carService;
    }

    /**
     * Rest-Endpoint to get crashEvents.
     * The type of DTO returned is dependent on the clienttype. There are DTOs for each of: Oem, Car, BlueLightOrg.
     * OEMs musst pass their oem as argument, because they can only see crashes of their cars. A authentication for this
     * was not required for this exercise.
     *
     * @param clientType Type of the client (Oem | Car | BlueLight)
     * @param oem        oem argument specifing the oem for Oem clients
     * @return List of crashevents including information specific to the ClientType.
     * @throws BadRequestException If the client-type-header was missing/unknown or a OEM did not pass the oem-param
     */
    @GetMapping("/notificationstorage/notifications")
    public List<?> getCrashEvents(@RequestHeader(value="X-Client-Type") String clientType,
                                  @RequestParam(required = false) Optional<String> oem) throws BadRequestException {
        if (CLIENT_TYPE_CAR.equals(clientType)) {
            LOGGER.info("Getting active crashevents for cars");
            // Get all active crashes
            List<CarNotificationDto> response = carService.getAllActiveCrashEvents();
            redisService.cache("/notificationstorage/notifications/car", response);
            return response;
        } else if (CLIENT_TYPE_OEM.equals(clientType)) {
            // Get all crashes by oem
            if (oem.isPresent()) {
                LOGGER.info("Getting crashevents for OEM " + oem);
                List<OemNotificationDto> resp = oemNotificaionService.getOemNotifications(oem.get());
                redisService.cache("/notificationstorage/notifications/" + oem.get(), resp);
                return resp;
            }
            throw new BadRequestException("Missing parameter \"oem\"!");
        } else if (CLIENT_TYPE_BLUELIGHT.equals(clientType)) {
            LOGGER.info("Getting all crashevents for BlueLightOrganisations");
            // Get all crashes
            List<BlueLightOrgNotificationDto> resp = blueLightOrganisationService.getAllAccidents();
            redisService.cache("/notificationstorage/notifications/bluelight", resp);
            return resp;
        } else {
            throw new BadRequestException("Missing header \"X-Client-Type\"");
        }
    }

    /**
     * Rest-endpoint to save a crashevent in the database.
     * With the stompService the UI-Components for oems, bluelightorg and cars are notified about the new crash.ö
     *
     * @param crashEventDto Information about the crash.
     */
    @PostMapping("/notificationstorage/notifications")
    public void createCrashEvent(@RequestBody CrashEventDto crashEventDto) {
        LOGGER.info("Creating new Crash with chassis {}", crashEventDto.getChassisNumber());
        CrashEvent event = new CrashEvent();
        event.setChassisnumber(crashEventDto.getChassisNumber());
        event.setCrashTimestamp(crashEventDto.getTimestamp());
        event.setDescription(crashEventDto.getDescription());
        event.setEventId(crashEventDto.getEventId());

        event = crashRepository.save(event);
        /* inform frontend components */
        stompService.yell(event);
    }

    /**
     * Rest-endpoint to resolve active crashevents.
     * With the stompService the UI-Compenents for oems, bluelightorg and cars are notified that the crash was resolved.
     *
     * @param crashId Id of the active crash.
     * @throws CrashNotFoundException If no crash with the given Id could be found.
     * @throws CrashAlreadyInactiveException If the crash has already been resolved.
     */
    @PatchMapping("/notificationstorage/notifications/{crashId}")
    public void resolveCrashEvent(@PathVariable String crashId) throws CrashNotFoundException, CrashAlreadyInactiveException {
        LOGGER.info("Resolving Crash with id {}", crashId);
        CrashEvent event = crashRepository.findById(crashId).orElse(null);

        /* check if crash was found */
        if (event == null) throw new CrashNotFoundException("crash with Id" + crashId + "not found");

        /* check if the crash is active */
        if (event.getResolveTimestamp() != null) throw new CrashAlreadyInactiveException("Crash with Id " + crashId + "is already inactive");

        /* resolve crash */
        event.setResolveTimestamp(new Date());
        event = crashRepository.save(event);

        /* inform frontend components */
        stompService.yell(event);
    }
}
