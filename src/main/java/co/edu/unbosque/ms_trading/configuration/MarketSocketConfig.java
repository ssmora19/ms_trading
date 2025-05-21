package co.edu.unbosque.ms_trading.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import co.edu.unbosque.ms_trading.component.MarketWebSocketHandler;

// WebSocketConfig.java
@Configuration
@EnableWebSocket
public class MarketSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(new MarketWebSocketHandler(), "/ws")
                .setAllowedOrigins("*"); // Asegúrate de permitir tu origen
    }
}
