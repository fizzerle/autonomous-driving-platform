package tuwien.dse.notificationstorageservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.BlueLightOrgNotificationDto;
import tuwien.dse.notificationstorageservice.dto.CarEventDto;
import tuwien.dse.notificationstorageservice.dto.Location;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlueLightOrganisationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueLightOrganisationService.class);

    @Autowired
    private CrashRepository crashRepository;

    @Autowired
    private EventStoreRestClient eventStoreRestClient;

    /**
     * Method which returns Dtos with crash-information seen by bluelightorgs for each crash.
     *
     * @return List of crashes
     */
    public List<BlueLightOrgNotificationDto> getAllAccidents() {
        List<BlueLightOrgNotificationDto> accidents = new ArrayList<>();

        LOGGER.info("Getting all crashevents from repo...");
        for(CrashEvent event: crashRepository.findAll()) {
            accidents.add(getBlueLightOrgNotificationDto(event));
        }
        LOGGER.info(accidents.size() + " accidents found");

        return accidents.stream().filter(c -> c != null).collect(Collectors.toList());
    }

    /**
     * Method to create a BlueLightOrgNotificationDto for a crash.
     * EventStoreRestClient is used to get the crashLocation and carinformation.
     *
     * @param crashEvent the crashInformation.
     * @return subset of information seen by blue-light-organisations.
     */
    private BlueLightOrgNotificationDto getBlueLightOrgNotificationDto(CrashEvent crashEvent) {
        CarEventDto event;

        /* get additional event information */
        try {
            event = eventStoreRestClient.getCarEvent(crashEvent.getEventId());
            if(event == null) return null;
        } catch (Exception e) {
            LOGGER.warn("Failure catching the Event with id {}", crashEvent.getEventId(), e);
            return null;
        }

        return new BlueLightOrgNotificationDto(
                crashEvent.getCrashId(),
                event.getOem(),
                crashEvent.getChassisnumber(),
                event.getModeltype(),
                event.getLocation(),
                crashEvent.getResolveTimestamp(),
                crashEvent.getCrashTimestamp(),
                event.getPassengers()
        );
    }

}
