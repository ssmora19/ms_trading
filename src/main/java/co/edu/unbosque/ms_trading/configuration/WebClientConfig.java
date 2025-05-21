package co.edu.unbosque.ms_trading.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${micro-service.user.base-url}")
    private String userUrl; // Cambia esto a la URL de tu API

    @Bean
    @Qualifier("webClientUser")
    public WebClient webClientUser() {
        return WebClient.builder()
                .baseUrl(userUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    

}
