package tuwien.dse.eventstorageservice.services;

import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tuwien.dse.eventstorageservice.dto.CrashEventDto;
import tuwien.dse.eventstorageservice.model.Event;

@Service
public class NotificationStoreRestClient {

    private NotificationService eventStoreService;

    public NotificationStoreRestClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8083")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        this.eventStoreService = retrofit.create(NotificationService.class);
    }

    public void createCrashEvent(Event event) throws Exception {
        CrashEventDto crash = new CrashEventDto(
                event.getChassisnumber(),
                event.getId(),
                event.getTimestamp(),
                event.getCrashEvent()
        );
        Call call = eventStoreService.createCrashEvent(crash);

        Response resp = call.execute();
        if (!resp.isSuccessful()) {
            throw new Exception(resp.message());
        }
    }

    interface NotificationService {

        @POST("/notifications")
        Call createCrashEvent(@Body CrashEventDto crashEvent);
    }
}
