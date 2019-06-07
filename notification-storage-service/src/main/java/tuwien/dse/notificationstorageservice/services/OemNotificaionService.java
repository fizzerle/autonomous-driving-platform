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

    /**
     * Method which returns Dtos with crash-information seen by oems for each crash.
     *
     * @return List of crashes
     */
    public List<OemNotificationDto> getOemNotifications(String oem) {
        List<OemNotificationDto> notifications = new ArrayList<>();
        LOGGER.info("Getting all crashes from DB");
        List<CrashEvent> events = crashRepository.findAll();
        LOGGER.info("{} crashes found", events.size());

        for (CrashEvent event: events) {
            CarEventDto carEventDto = getCarEvent(event);
            if (carEventDto != null && carEventDto.getOem().toLowerCase().equals(oem.toLowerCase())) {
                notifications.add(getOemNotificationDto(event, carEventDto));
            }
        }
        LOGGER.info("returning {} crashes for oem {}", notifications.size(), oem);
        return notifications;
    }


    /**
     * Method to get event information for a crashEvent.
     * @param crashEvent crashEventInfo
     * @return Event information
     */
    private CarEventDto getCarEvent(CrashEvent crashEvent) {
        try {
            return eventStoreRestClient.getCarEvent(crashEvent.getEventId());
        } catch (Exception e) {
            LOGGER.warn("Failure catching the Event with id {}", crashEvent.getEventId(), e);
            return null;
        }
    }

    /**
     * Method to create a OemNotificationDto for a crash.
     *
     * @param crashEvent the crashInformation.
     * @param carEvent the car and event Information.
     * @return combined of information seen by oems
     */
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
