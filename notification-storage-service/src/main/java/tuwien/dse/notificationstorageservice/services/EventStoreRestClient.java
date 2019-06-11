package tuwien.dse.notificationstorageservice.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class EventStoreRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueLightOrganisationService.class);

    private EventStoreService eventStoreService;

    private RedisService redisService;

    private final String BASEURL = "/eventstorage/events/";

    /**
     * Constructor for a Restclient to call the EventStorageService.
     */
    public EventStoreRestClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://event-storage")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
        this.eventStoreService = retrofit.create(EventStoreService.class);
    }

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Method to call the Rest-method to get eventinformation by the eventId of the EventService.
     *
     * @param eventId Id of the event.
     * @return Event-information for the information.
     * @throws Exception If the call could not be executed successfully.
     */
    @HystrixCommand(fallbackMethod = "eventFallback")
    public CarEventDto getCarEvent(String eventId) throws Exception {
        LOGGER.info("Making restcall getEvent for " + eventId);
        Call<CarEventDto> call = eventStoreService.getCarData(eventId);
        Response<CarEventDto> resp = call.execute();
        if (!resp.isSuccessful()) {
            throw new Exception(resp.message());
        }
        return resp.body();
    }

    private CarEventDto eventFallback(String eventId) {
        Gson gson = new Gson();
        return gson.fromJson(redisService.getCache(BASEURL + eventId), CarEventDto.class);
    }

    /**
     * Method to call the Rest-method to get all cars with most recent location within a 3km radius of the given location.
     * @param location Location representing the 3 km circle.
     * @return List of Cars within the circle.
     */
    @HystrixCommand(fallbackMethod = "affectedFallback")
    public List<String> getAffectedCars(Location location) {
        LOGGER.info("Makeing restcall getCarsIn3kmRadius at location {} {}", location.getLat(), location.getLng());
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

    private List<String> affectedFallback(Location location) {
        String url = BASEURL + "radius?lng=" + location.getLng() + "&lat=" + location.getLat();
        Gson gson = new Gson();
        String cache = redisService.getCache(url);
        if(cache == null) return null;
        return gson.fromJson(redisService.getCache(url), new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    interface EventStoreService {

        /**
         * Rest-endpoint to get eventData by the given id.
         * @param id EventId
         * @return Event-information
         */
        @GET("/eventstorage/events/{id}")
        Call<CarEventDto> getCarData(@Path("id") String id);

        /**
         * Rest-endpoint to get all cars with their most recent position within 3 km of the given position.
         * @param lat Latitude of the center of the 3 km circle queried.
         * @param lng Longitude of the center of the 3 km circle queried.
         * @return Cars within the circle.
         */
        @GET("/eventstorage/events/radius")
        Call<List<String>> getAffectedCars(@Query("lat") double lat, @Query("lng") double lng);
    }
}
