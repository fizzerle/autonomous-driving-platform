package tuwien.dse.notificationstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.BlueLightOrgNotificationDto;
import tuwien.dse.notificationstorageservice.dto.CarNotificationDto;
import tuwien.dse.notificationstorageservice.dto.OemNotificationDto;
import tuwien.dse.notificationstorageservice.model.CrashEvent;

@Service
public class CrashNotifyService {

    @Autowired
    private SimpMessageSendingOperations simp;

    public void yell(CrashEvent crashEvent) {
        // TODO: Get all needed data, and notify cars, bluelights, oem
    }

    public void notifyCars(CarNotificationDto data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/car";
            simp.convertAndSend(topic, json);
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
