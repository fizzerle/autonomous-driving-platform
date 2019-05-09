package tuwien.dse.notificationstorageservice.services;

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

    @Autowired
    private EventStoreRestClient eventStoreRestClient;

    @Autowired
    private CrashRepository repo;

    public List<CarNotificationDto> getAllActiveCrashEvents() {
        return repo.findAll().stream()
                .filter(c -> c.getResolveTimestamp() == null)
                .map(c -> getCarNotificationDto(c))
                .collect(Collectors.toList());
    }

    private CarNotificationDto getCarNotificationDto(CrashEvent crash) {
        CarEventDto event;

        try {
            event = eventStoreRestClient.getCarEvent(crash.getEventId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new CarNotificationDto(
                event.getLocation(),
                crash.getResolveTimestamp() == null
        );
    }
}
