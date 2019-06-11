package tuwien.dse.notificationstorageservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.CarEventDto;
import tuwien.dse.notificationstorageservice.dto.CarNotificationDto;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutonomousCarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutonomousCarService.class);

    @Autowired
    private EventStoreRestClient eventStoreRestClient;

    @Autowired
    private CrashRepository repo;

    /**
     * Method which returns Dtos with crash-information seen by cars for each active crash.
     *
     * @return List of active crashes
     */
    public List<CarNotificationDto> getAllActiveCrashEvents() {
        LOGGER.info("Getting car notifications");
        return repo.findAll().stream()
                .filter(c -> c.getResolveTimestamp() == null)
                .map(c -> getCarNotificationDto(c))
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    /**
     * Method to create a CarNotificationDto for a crash.
     * EventStoreRestClient is used to get the crashLocation.
     *
     * @param crash the crashInformation.
     * @return subset of information seen by cars.
     */
    private CarNotificationDto getCarNotificationDto(CrashEvent crash) {
        CarEventDto event;

        /* get event information */
        try {
            event = eventStoreRestClient.getCarEvent(crash.getEventId());
            if(event == null) return null;
        } catch (Exception e) {
            LOGGER.warn("Failure catching the Event with id {}", crash.getEventId(), e);
            return null;
        }

        return new CarNotificationDto(
                event.getLocation(),
                crash.getResolveTimestamp() == null
        );
    }
}
