package tuwien.dse.notificationstorageservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.CarEventDto;
import tuwien.dse.notificationstorageservice.dto.Location;
import tuwien.dse.notificationstorageservice.dto.OemNotificationDto;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OemNotificaionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OemNotificaionService.class);

    @Autowired
    private CrashRepository crashRepository;

    @Autowired
    private EventStoreRestClient eventStoreRestClient;

    public List<OemNotificationDto> getOemNotifications(String oem) {
        List<OemNotificationDto> notifications = new ArrayList<>();
        List<CrashEvent> events = crashRepository.findAll();

        for (CrashEvent event: events) {
            CarEventDto carEventDto = getCarEvent(event);
            if (carEventDto != null && carEventDto.getOem().toLowerCase().equals(oem.toLowerCase())) {
                notifications.add(getOemNotificationDto(event, carEventDto));
            }
        }
        return notifications;
    }

    private CarEventDto getCarEvent(CrashEvent crashEvent) {
        try {
            return eventStoreRestClient.getCarEvent(crashEvent.getEventId());
        } catch (Exception e) {
            LOGGER.warn("Failure catching the Event with id {}", crashEvent.getEventId(), e);
            return null;
        }
    }

    private OemNotificationDto getOemNotificationDto(CrashEvent crashEvent, CarEventDto carEvent) {
        return new OemNotificationDto(
                crashEvent.getCrashId(),
                crashEvent.getCrashTimestamp(),
                crashEvent.getDescription(),
                crashEvent.getChassisnumber(),
                crashEvent.getResolveTimestamp(),
                carEvent.getLocation()
        );
    }

}
