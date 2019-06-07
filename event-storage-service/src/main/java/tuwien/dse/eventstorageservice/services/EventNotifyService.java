package tuwien.dse.eventstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import tuwien.dse.eventstorageservice.dto.CarEventDto;
import tuwien.dse.eventstorageservice.exception.EventNotFoundException;

/**
 * Service to notify others about eventUpdates with a websocket.
 */
@Service
public class EventNotifyService {

    @Autowired
    private SimpMessageSendingOperations simp;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotifyService.class);
    /**
     * Sends the new CarEvent with the websocket.
     *
     * @param data Eventdata.
     */
    public void yell(CarEventDto data) {
        LOGGER.info("Sending positionUpdate via websocket");
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
