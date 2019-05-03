package tuwien.dse.notificationstorageservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.OemNotificationDto;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OemNotificaionService {

    @Autowired
    CrashRepository crashRepository;

    public List<OemNotificationDto> getOemNotifications(String oem) {
        List<OemNotificationDto> notifications = new ArrayList<>();
        List<CrashEvent> events = crashRepository.findAll();

        for (CrashEvent event: events) {
            //TODO: filter oem
            notifications.add(new OemNotificationDto(event.getCrashTimestamp(), event.getDescription(), event.getChassisnumber()));
        }
        return notifications;
    }

}
