package co.edu.unbosque.ms_trading.component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unbosque.ms_trading.model.dto.websocket.BarMessage;
import co.edu.unbosque.ms_trading.model.dto.websocket.TradeMessage;
import co.edu.unbosque.ms_trading.service.AlpacaService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.bar.StockBarMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.trade.StockTradeMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.streams.stock.StockMarketDataListenerAdapter;

@Component
public class Market {

    @Autowired
    private AlpacaService alpacaService;

    @Autowired
    private MarketWebSocketHandler handler;

     /* @PostConstruct
    public void startMarketDataStream() {
        AlpacaAPI alpacaAPI = alpacaService.getAlpacaApi();
        new Thread(() -> {
            try {
                if (!alpacaAPI.stockMarketDataStream().isConnected()) {
                    alpacaAPI.stockMarketDataStream().connect();
                }
                // alpacaAPI.stockMarketDataStream().connect();

                if (!alpacaAPI.stockMarketDataStream().waitForAuthorization(5, TimeUnit.SECONDS)) {
                    throw new RuntimeException("No se pudo autorizar el stream.");
                }

                ObjectMapper objectMapper = new ObjectMapper();

                alpacaAPI.stockMarketDataStream().setListener(new StockMarketDataListenerAdapter() {
                    @Override
                    public void onTrade(StockTradeMessage trade) {
                        try {
                            TradeMessage tradeMessage = TradeMessage.builder()
                                    .symbol(trade.getSymbol())
                                    .price(trade.getPrice())
                                    .size(trade.getSize())
                                    .timestamp(trade.getTimestamp().toString())
                                    .build();

                            String json = objectMapper.writeValueAsString(tradeMessage);
                            System.out.println("Trade: " + json);
                            handler.sendMessageToAll(json);


                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMinuteBar(StockBarMessage minuteBar) {
                        try {
                            BarMessage minuteBarMessage = BarMessage.builder()
                                    .symbol(minuteBar.getSymbol())
                                    .time(minuteBar.getTimestamp().toString())
                                    .open(minuteBar.getOpen())
                                    .high(minuteBar.getHigh())
                                    .low(minuteBar.getLow())
                                    .close(minuteBar.getClose())
                                    .volume(minuteBar.getVolume())
                                    .build();

                            String json = objectMapper.writeValueAsString(minuteBarMessage);
                            System.out.println("Minute Bar: " + json);
                            handler.sendMessageToAll(json);

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                System.out.println("Martket WebSocket de Alpaca conectado y autorizado.");

            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }*/

    public synchronized void subscribeToSymbol(String symbol) {
        try {
            // Update both trade and minute bar subscriptions
            Set<String> currentSubscriptions = new HashSet<>(alpacaService.getAlpacaApi().stockMarketDataStream()
                    .getTradeSubscriptions());
            currentSubscriptions.add(symbol);
            alpacaService.getAlpacaApi().stockMarketDataStream().setTradeSubscriptions(currentSubscriptions);
            alpacaService.getAlpacaApi().stockMarketDataStream().setMinuteBarSubscriptions(currentSubscriptions);
            System.out.println("Suscripción activa a trades y minute bars de " + symbol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribeToSymbol(Set<String> symbols) {
        try {
            // Update both trade and minute bar subscriptions
            Set<String> currentSubscriptions = new HashSet<>(alpacaService.getAlpacaApi().stockMarketDataStream()
                    .getTradeSubscriptions());
            currentSubscriptions.addAll(symbols);
            alpacaService.getAlpacaApi().stockMarketDataStream().setTradeSubscriptions(currentSubscriptions);
            alpacaService.getAlpacaApi().stockMarketDataStream().setMinuteBarSubscriptions(currentSubscriptions);
            System.out.println("Suscripción activa a trades y minute bars de los símbolos: " + symbols);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void unsubscribeSymbol(String symbol) {
        try {
            // Get current subscriptions (same for both trades and minute bars)
            Set<String> currentSubscriptions = new HashSet<>(alpacaService.getAlpacaApi().stockMarketDataStream()
                    .getTradeSubscriptions());

            // Remove the symbol from the subscriptions
            currentSubscriptions.remove(symbol);

            // Update the subscriptions for both trades and minute bars
            alpacaService.getAlpacaApi().stockMarketDataStream().setTradeSubscriptions(currentSubscriptions);
            alpacaService.getAlpacaApi().stockMarketDataStream().setMinuteBarSubscriptions(currentSubscriptions);

            System.out.println("Desuscripción realizada para trades y minute bars de " + symbol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void unsubscribeSymbol(Set<String> symbols) {
        try {
            // Get current subscriptions (same for both trades and minute bars)
            Set<String> currentSubscriptions = new HashSet<>(alpacaService.getAlpacaApi().stockMarketDataStream()
                    .getTradeSubscriptions());

            // Remove the symbols from the subscriptions
            currentSubscriptions.removeAll(symbols);

            // Update the subscriptions for both trades and minute bars
            alpacaService.getAlpacaApi().stockMarketDataStream().setTradeSubscriptions(currentSubscriptions);
            alpacaService.getAlpacaApi().stockMarketDataStream().setMinuteBarSubscriptions(currentSubscriptions);

            System.out.println("Desuscripción realizada para trades y minute bars de los símbolos: " + symbols);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void shutdown() {
        try {
            System.out.println("Cerrando WebSocket de Alpaca...");
            alpacaService.getAlpacaApi().stockMarketDataStream().disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
