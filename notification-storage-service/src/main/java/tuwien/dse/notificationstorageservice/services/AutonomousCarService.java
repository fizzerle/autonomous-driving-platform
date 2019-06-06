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

    public List<CarNotificationDto> getAllActiveCrashEvents() {
        return repo.findAll().stream()
                .filter(c -> c.getResolveTimestamp() == null)
                .map(c -> getCarNotificationDto(c))
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    private CarNotificationDto getCarNotificationDto(CrashEvent crash) {
        CarEventDto event;

        try {
            event = eventStoreRestClient.getCarEvent(crash.getEventId());
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
