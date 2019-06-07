package tuwien.dse.eventstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import tuwien.dse.eventstorageservice.dto.CarEventDto;

/**
 * Service to notify others about eventUpdates with a websocket.
 */
@Service
public class EventNotifyService {

    @Autowired
    private SimpMessageSendingOperations simp;

    /**
     * Sends the new CarEvent with the websocket.
     *
     * @param data Eventdata.
     */
    public void yell(CarEventDto data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/event/" + data.getOem().toLowerCase();
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            // Do nothing
        }
    }
}
