package co.edu.unbosque.ms_trading.component;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unbosque.ms_trading.model.dto.websocket.BarMessage;
import co.edu.unbosque.ms_trading.model.dto.websocket.TradeMessage;
import jakarta.annotation.PreDestroy;

@Component
public class MarketSimulator {
    
    @Autowired
    private MarketWebSocketHandler handler;

    private Boolean running = false;
    private Thread simulatorThread;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public void startSimulation() {
        if (running) {
            System.out.println("Simulación ya está corriendo.");
            return;
        }

        running = true;

        simulatorThread = new Thread(() -> {
            System.out.println("Iniciando simulación de datos de mercado...");
            while (running) {
                try {
                    // Simular trade
                    TradeMessage tradeMessage = TradeMessage.builder()
                            .symbol("FAKE")
                            .price(100 + random.nextDouble() * 10)
                            .size(1 + random.nextInt(100))
                            .timestamp(Instant.now().toString())
                            .build();

                    String tradeJson = objectMapper.writeValueAsString(tradeMessage);
                    // System.out.println("Simulated Trade: " + tradeJson);
                    handler.sendMessageToAll(tradeJson);

                    // Simular minute bar
                    double open = 100 + random.nextDouble() * 10;
                    double close = open + (random.nextDouble() * 2 - 1);
                    double high = Math.max(open, close) + random.nextDouble();
                    double low = Math.min(open, close) - random.nextDouble();

                    BarMessage barMessage = BarMessage.builder()
                            .symbol("FAKE")
                            .time(Instant.now().toString())
                            .open(open)
                            .high(high)
                            .low(low)
                            .close(close)
                            .volume(100 + random.nextInt(1000))
                            .build();

                    String barJson = objectMapper.writeValueAsString(barMessage);
                    // System.out.println("Simulated Minute Bar: " + barJson);
                    handler.sendMessageToAll(barJson);

                    TimeUnit.SECONDS.sleep(1); // espera 1 segundo entre envíos
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simulatorThread.start();
    }

    public void stopSimulation() {
        running = false;
        if (simulatorThread != null) {
            simulatorThread.interrupt();
        }
        System.out.println("Simulación detenida.");
    }

    @PreDestroy
    public void shutdown() {
        stopSimulation();
    }
}
