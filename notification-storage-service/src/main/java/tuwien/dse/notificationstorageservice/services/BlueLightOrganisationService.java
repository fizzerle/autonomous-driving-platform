package tuwien.dse.notificationstorageservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.BlueLightOrgNotificationDto;
import tuwien.dse.notificationstorageservice.dto.Location;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlueLightOrganisationService {

    @Autowired
    CrashRepository crashRepository;

    public List<BlueLightOrgNotificationDto> getAllAccidents() {
        List<BlueLightOrgNotificationDto> accidents = new ArrayList<>();

        for(CrashEvent event: crashRepository.findAll()) {
            //TODO: get car and location information from Entity and Event service
            accidents.add(new BlueLightOrgNotificationDto(
                    event.getCrashId(),
                    "Audi",
                    event.getChassisnumber(),
                    "A8",
                    new Location(0,0),
                    event.getResolveTimestamp(),
                    event.getCrashTimestamp(),
                    1
            ));
        }

        return accidents;
    }

    public List<BlueLightOrgNotificationDto> getAllActiveAccidents() {
        List<BlueLightOrgNotificationDto> accidents = new ArrayList<>();

        for(CrashEvent event: crashRepository.findAll()) {
            if (event.getResolveTimestamp() == null) {
                //TODO: get car and location information from Entity and Event service
                accidents.add(new BlueLightOrgNotificationDto(
                        event.getCrashId(),
                        "Audi",
                        event.getChassisnumber(),
                        "A8",
                        new Location(0,0),
                        event.getResolveTimestamp(),
                        event.getCrashTimestamp(),
                        1
                ));
            }
        }

        return accidents;
    }

}
