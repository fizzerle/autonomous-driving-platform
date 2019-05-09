package tuwien.dse.notificationstorageservice.services;

import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tuwien.dse.notificationstorageservice.dto.CarEventDto;

@Service
public class EventStoreRestClient {

    private EventStoreService eventStoreService;

    public EventStoreRestClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8081")
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

    interface EventStoreService {

        @GET("/events/{id}")
        Call<CarEventDto> getCarData(@Path("id") String id);
    }
}
