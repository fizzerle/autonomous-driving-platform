package tuwien.dse.notificationstorageservice.services;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tuwien.dse.notificationstorageservice.dto.CarEventDto;
import tuwien.dse.notificationstorageservice.dto.Location;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class EventStoreRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueLightOrganisationService.class);

    private EventStoreService eventStoreService;

    public EventStoreRestClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://event-storage")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
        this.eventStoreService = retrofit.create(EventStoreService.class);
    }

    public CarEventDto getCarEvent(String eventId) throws Exception {
        Call<CarEventDto> call = eventStoreService.getCarData(eventId);
        Response<CarEventDto> resp = call.execute();
        if (!resp.isSuccessful()) {
            throw new Exception(resp.message());
        }
        return resp.body();
    }

    public List<String> getAffectedCars(Location location) {
        Call<List<String>> call = eventStoreService.getAffectedCars(location.getLat(), location.getLng());
        Response<List<String>> resp = null;
        try {
            resp = call.execute();
            if (!resp.isSuccessful()) {
                throw new Exception(resp.message());
            }
        } catch (Exception e) {
            LOGGER.warn("Failure catching the affected cars", e);
            return new LinkedList<>();
        }
        return resp.body();
    }

    interface EventStoreService {

        @GET("/eventstorage/events/{id}")
        Call<CarEventDto> getCarData(@Path("id") String id);

        @GET("/eventstorage/events/radius")
        Call<List<String>> getAffectedCars(@Query("lat") double lat, @Query("lng") double lng);
    }
}
