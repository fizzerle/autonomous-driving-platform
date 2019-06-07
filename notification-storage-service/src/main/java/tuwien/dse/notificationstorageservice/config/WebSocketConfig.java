package tuwien.dse.notificationstorageservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Defines the notificationservice message broker with the application destination endpoint.
     *
     * @param config MessageBrokerRegistry.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/crash");
        config.setApplicationDestinationPrefixes("/notificationservice");
    }

    /**
     * Defines the websocket endpoint for stomp notifications over sockJS.
     *
     * @param registry StompEndpointRegistry.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notificationstorage/websocket").setAllowedOrigins("*").withSockJS();
    }
}
