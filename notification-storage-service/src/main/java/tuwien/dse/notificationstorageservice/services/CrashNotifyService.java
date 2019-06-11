package tuwien.dse.notificationstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CrashNotifyService.class);

    @Autowired
    private EventStoreRestClient eventStoreRestClient;

    @Autowired
    private SimpMessageSendingOperations simp;

    /**
     * Method to send notifications for a new Crashevent to all differnt frontendcomponents which have insight into crashes.
     * Specific Dtos for the oems, bluelightorgs and cars are created.
     * The EventStoreRestClient is used to enrich the crashInvormation with location and car infos.
     *
     * @param crashEvent
     */
    public void yell(CrashEvent crashEvent) {
        CarEventDto event;

        /* get event-information */
        try {
            event = eventStoreRestClient.getCarEvent(crashEvent.getEventId());
            if(event == null) return;
        } catch (Exception e) {
            LOGGER.warn("Could not fetch data from eventstore", e);
            return;
        }

        /* create Dto for cars */
        CarNotificationDto carDto = new CarNotificationDto(
                event.getLocation(),
                crashEvent.getResolveTimestamp() == null
        );

        /* create Dto for bluelightorgs */
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

        /* create Dto for oem */
        OemNotificationDto oemDto = new OemNotificationDto(
                crashEvent.getCrashId(),
                crashEvent.getCrashTimestamp(),
                crashEvent.getDescription(),
                crashEvent.getChassisnumber(),
                crashEvent.getResolveTimestamp(),
                event.getLocation()
        );

        /* notify the frontend components */
        notifyCars(carDto);
        notifyBluelights(bluelightDto);
        notifyOem(event.getOem(), oemDto);
    }


    /**
     * Method to notify car-ui-views about a new crash through a websocket.
     *
     * @param data Crash-infos which can seen by cars.
     */
    public void notifyCars(CarNotificationDto data) {
        LOGGER.info("Notifying cars about crash at {) {}", data.getLocation().getLat(), data.getLocation().getLng());
        try {
            List<String> affectedCars = eventStoreRestClient.getAffectedCars(data.getLocation());
            if(affectedCars ==  null) return;
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            for (String chassis: affectedCars) {
                String topic = "/crash/car/" + chassis;
                simp.convertAndSend(topic, json);
            }
        } catch (JsonProcessingException e) {
            LOGGER.warn("Could not send Notification to affected cars", e);
        }
    }

    /**
     * Method to notify bluelight-ui-views about a new crash through a websocket.
     * @param data Crash-infos which can seen by blue-light-organisations.
     */
    public void notifyBluelights(BlueLightOrgNotificationDto data) {
        LOGGER.info("Notifying blueLightOrg about crash of {}", data.getChassisnumber());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/bluelight";
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Could not send Notification to Bluelight", e);
        }
    }

    /**
     * Method to notify oem-ui-views about a new crash through a websocket.
     * @param data Crash-infos which can seen by oems.
     */
    public void notifyOem(String oem, OemNotificationDto data) {
        LOGGER.info("Notifying OEM about crash of {}", data.getChassisnumber());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            String topic = "/crash/" + oem.toLowerCase();
            simp.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Could not send Notification to OEM", e);
        }
    }
}
