package tuwien.dse.swaggergateway.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class GatewayApi {
    @Autowired
    ZuulProperties properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApi.class);

    /**
     * Aggregate all Swagger endpoints of microservices
     * @return all Swagger Resources
     */
    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        LOGGER.info("LOCATION1: .------------------------------------");
        return () -> {
            List<SwaggerResource> resources = new ArrayList<>();
            properties.getRoutes().values().stream()
                    .forEach(route -> resources.add(createResource(route.getId(), "2.0")));
            return resources;
        };
    }

    /**
     * Creates the Swagger resources
     * @param location where the resource can be found
     * @param version version of the resource
     * @return the resources
     */
    private SwaggerResource createResource(String location, String version) {
        LOGGER.info("LOCATION: "+ location);
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(location);
        swaggerResource.setLocation("/" + location + "/v2/api-docs");
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
