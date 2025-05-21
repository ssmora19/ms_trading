package co.edu.unbosque.ms_trading.component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;


@Component
public class MarketWebSocketHandler /* extends TextWebSocketHandler */ implements WebSocketHandler {

    // private final Map<WebSocketSession, Set<String>> suscripciones = new ConcurrentHashMap<>();

    private static final Set<WebSocketSession> sesiones = new CopyOnWriteArraySet<>();
    // private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sesiones.add(session);
        // System.out.println("Sesión añadida: " + session.getId());  // Log para verificar
        // System.out.println(sesiones.size());
        session.sendMessage(new TextMessage("Conexión realizada con éxito"));
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
     
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        System.err.println("Error de transporte en la sesión: " + session.getId());
        exception.printStackTrace();

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }

        sesiones.remove(session);
        // suscripciones.remove(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        sesiones.remove(session);
        // suscripciones.remove(session);
        System.out.println("Sesión cerrada: " + session.getId());

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessageToAll(String message) {
        // System.out.println("Numero de sesiones activas: " + sesiones.size());
        if(sesiones.isEmpty()){
            System.out.println("No hay sesiones activas");
            return;
        }
        for (WebSocketSession session : sesiones) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}