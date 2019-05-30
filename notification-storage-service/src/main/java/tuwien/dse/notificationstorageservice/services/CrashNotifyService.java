package tuwien.dse.notificationstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.BlueLightOrgNotificationDto;
import tuwien.dse.notificationstorageservice.dto.CarEventDto;
import tuwien.dse.notificationstorageservice.dto.CarNotificationDto;
import tuwien.dse.notificationstorageservice.dto.OemNotificationDto;
import tuwien.dse.notificationstorageservice.model.CrashEvent;

import java.util.List;


@Service
public class CrashNotifyService {

    @Autowired
    private EventStoreRestClient eventStoreRestClient;

    @Autowired
    private SimpMessageSendingOperations simp;

    public void yell(CrashEvent crashEvent) {
        CarEventDto event;

        try {
            event = eventStoreRestClient.getCarEvent(crashEvent.getEventId());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        CarNotificationDto carDto = new CarNotificationDto(
                event.getLocation(),
                crashEvent.getResolveTimestamp() == null
        );
        BlueLightOrgNotificationDto bluelightDto = new BlueLightOrgNotificationDto(
                crashEvent.getCrashId(),
                event.getOem(),
                crashEvent.getChassisnumber(),
                event.getModeltype(),
                event.getLocation(),
                crashEvent.getResolveTimestamp(),
                crashEvent.getCrashTimestamp(),
                event.getPassengers()
        );
        OemNotificationDto oemDto = new OemNotificationDto(
                crashEvent.getCrashId(),
                crashEvent.getCrashTimestamp(),
                crashEvent.getDescription(),
                crashEvent.getChassisnumber(),
                crashEvent.getResolveTimestamp(),
                event.getLocation()
        );

        notifyCars(carDto);
        notifyBluelights(bluelightDto);
        notifyOem(event.getOem(), oemDto);
    }


    public void notifyCars(CarNotificationDto data) {
        try {
            List<String> affectedCars = eventStoreRestClient.getAffectedCars(data.getLocation());
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            for (String chassis: affectedCars) {
                String topic = "/crash/car/" + chassis;
                simp.convertAndSend(topic, json);
            }
        } catch (JsonProcessingException e) {
            // Do nothing
        }
    }

    public void notifyBluelights(BlueLightOrgNotificationDto data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/bluelight";
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            // Do nothing
        }
    }

    public void notifyOem(String oem, OemNotificationDto data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/" + oem.toLowerCase();
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            // Do nothing
        }
    }
}
