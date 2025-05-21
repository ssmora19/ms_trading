package co.edu.unbosque.ms_trading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ApiService {
    
    @Autowired
    @Qualifier("webClientUser")
    private WebClient webClientUser;


    public String getUserData() {
        String resp = webClientUser.get()
                .uri("/")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(resp);
        return resp;
    }
    
}
