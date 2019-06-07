package tuwien.dse.eventstorageservice.services;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import tuwien.dse.eventstorageservice.dto.CrashEventDto;
import tuwien.dse.eventstorageservice.model.Event;

@Service
public class NotificationStoreRestClient {

    private NotificationService notificationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationStoreRestClient.class);

    /**
     * Constructor for a Restclient which can send request to the NotificationStorageService.
     */
    public NotificationStoreRestClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://notification-storage")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
        this.notificationService = retrofit.create(NotificationService.class);
    }

    /**
     * Method to call the createCrashEvent-Method of the NotificationStorageService.
     * Creates a new unresolved CrashEvent.
     *
     * @param event Information describing the Event.
     * @throws Exception If the NotificationStorageService call was not successful.
     */
    public void createCrashEvent(Event event) throws Exception {
        LOGGER.info("Making restcall createCrashEvent for crashed car: " + event.getChassisnumber());
        CrashEventDto crash = new CrashEventDto(
                event.getChassisnumber(),
                event.getId(),
                event.getTimestamp(),
                event.getCrashEvent()
        );
        Call<Void> call = notificationService.createCrashEvent(crash);

        Response<Void> resp = call.execute();
        if (!resp.isSuccessful()) {
            throw new Exception(resp.message());
        }
    }

    interface NotificationService {

        /**
         * Rest-endpoint of the NotificationService which is used to create crashEvents.
         *
         * @param crash Information for the crashevent.
         * @return void
         */
        @POST("/notificationstorage/notifications")
        Call<Void> createCrashEvent(@Body CrashEventDto crash);
    }
}
