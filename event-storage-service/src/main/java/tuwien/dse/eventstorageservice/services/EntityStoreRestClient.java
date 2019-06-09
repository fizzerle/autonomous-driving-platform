package tuwien.dse.eventstorageservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tuwien.dse.eventstorageservice.dto.CarDto;
import tuwien.dse.eventstorageservice.dto.CrashEventDto;
import tuwien.dse.eventstorageservice.model.Event;

@Service
public class EntityStoreRestClient {

    private EntityService entityService;

    private RedisService redisService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityStoreRestClient.class);

    private static final String BASEURL = "/entitystorage/cars/";

    /**
     * Constructor for a RestClient which can send requests to the EntityStorage.
     */
    public EntityStoreRestClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://entity-storage")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
        this.entityService = retrofit.create(EntityService.class);
    }

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }



    /**
     * Method to invoke the getCar method of the EntityStorageService to get information to a car identified by the
     * chassisnumber.
     *
     * @param chassis Number identifying the car's chassis.
     * @return CarDto describing the car.
     * @throws Exception If the Rest-Endpoint could not be reached successfully.
     */
    @HystrixCommand(fallbackMethod = "carFallback")
    public CarDto getCar(String chassis) throws Exception {
        Call<CarDto> call = entityService.getCarData(chassis);

        LOGGER.info("Making restcall getCar for " + chassis);
        Response<CarDto> resp = call.execute();
        if (!resp.isSuccessful()) {
            throw new Exception(resp.message());
        }
        return resp.body();
    }

    private CarDto carFallback(String chassis) {
        Gson gson = new Gson();
        return gson.fromJson(redisService.getCache(BASEURL + chassis), CarDto.class);
    }


    interface EntityService {

        /**
         * Rest-endpoint of the Entityservice, which returns information for a car identified by its chassinumber.
         *
         * @param chassis Number identifying the car's chassis.
         * @return CarDto describing the car.
         */
        @GET("/entitystorage/cars/{chassis}")
        Call<CarDto> getCarData(@Path("chassis") String chassis);
    }
}
