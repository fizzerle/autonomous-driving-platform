package tuwien.dse.notificationstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import tuwien.dse.notificationstorageservice.dto.CrashDto;

@Service
public class CrashNotifyService {

    @Autowired
    private SimpMessageSendingOperations simp;

    public void yell(CrashDto data) {
        notifyBluelights(data);
        notifyOem(data);

        CrashDto carDto = new CrashDto();
        carDto.setLocation(data.getLocation());
        carDto.setActive(data.isActive());
        notifyCars(carDto);
    }

    public void notifyCars(CrashDto data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/car";
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            // Do nothing
        }
    }

    public void notifyBluelights(CrashDto data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/bluelight";
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            // Do nothing
        }
    }

    public void notifyOem(CrashDto data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/audi";
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            // Do nothing
        }
    }
}
