package tuwien.dse.notificationstorageservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.BlueLightOrgNotificationDto;
import tuwien.dse.notificationstorageservice.dto.CarEventDto;
import tuwien.dse.notificationstorageservice.dto.Location;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlueLightOrganisationService {

    @Autowired
    private CrashRepository crashRepository;

    @Autowired
    private EventStoreRestClient eventStoreRestClient;

    public List<BlueLightOrgNotificationDto> getAllAccidents() {
        List<BlueLightOrgNotificationDto> accidents = new ArrayList<>();

        for(CrashEvent event: crashRepository.findAll()) {
            //TODO: get car and location information from Entity and Event service
            accidents.add(getBlueLightOrgNotificationDto(event));
        }

        return accidents;
    }

    public List<BlueLightOrgNotificationDto> getAllActiveAccidents() {
        List<BlueLightOrgNotificationDto> accidents = new ArrayList<>();

        for(CrashEvent event: crashRepository.findAll()) {
            if (event.getResolveTimestamp() == null) {
                accidents.add(getBlueLightOrgNotificationDto(event));
            }
        }

        return accidents;
    }

    private BlueLightOrgNotificationDto getBlueLightOrgNotificationDto(CrashEvent crashEvent) {
        CarEventDto event;

        try {
            event = eventStoreRestClient.getCarEvent(crashEvent.getEventId());
        } catch (Exception e) {
            e.printStackTrace();
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
