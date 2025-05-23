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

    public String enviarSaldo(String email) {
        String url = "/enviarSaldo/" + email;
        String resp = webClientUser.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("Usuario no encontrado")
                        .map(body -> new RuntimeException("404 Not Found: " + body))
                )
                .bodyToMono(String.class)
                .onErrorReturn("Usuario no encontrado")
                .block();
        System.out.println(resp);
        return resp;
    }


    public String actualizarSaldo(String email, double saldo) {
        String url = "/" + email + "/saldo?saldo=" + saldo;
        String resp = webClientUser.patch()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("Usuario no encontrado")
                        .map(body -> new RuntimeException("404 Not Found: " + body))
                )
                .bodyToMono(String.class)
                .onErrorReturn("Usuario no encontrado")
                .block();
        System.out.println(resp);
        return resp;
    }
}
