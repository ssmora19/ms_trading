package co.edu.unbosque.ms_trading.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.edu.unbosque.ms_trading.excepcion.StockNotFoundException;
import jakarta.annotation.PostConstruct;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import net.jacobpeterson.alpaca.openapi.broker.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.Assets;
import net.jacobpeterson.alpaca.rest.marketdata.AlpacaMarketDataAPI;
import net.jacobpeterson.alpaca.rest.trader.AlpacaTraderAPI;

@Service
public class AlpacaService {
 
    @Value("${alpaca.api.key-id}")
    private String apiKey;

    @Value("${alpaca.api.secret-key}")
    private String secretKey;

    private AlpacaAPI alpacaApi; // Usamos AlpacaApi

    @PostConstruct
    public void init() {
        // Inicializa el cliente de AlpacaApi
        alpacaApi =  AlpacaAPI.builder()
                            .withTraderKeyID(apiKey)
                            .withTraderSecretKey(secretKey)
                            .withTraderAPIEndpointType(TraderAPIEndpointType.PAPER)
                            .build();
        System.out.println("AlpacaApi initialized successfully for URL: "); // Log para verificar
    }

 
    public AlpacaTraderAPI getTraderAPI() {
        return alpacaApi.trader();
    }

    public AlpacaMarketDataAPI getMarketDataAPI() {
        return alpacaApi.marketData();
    }

    public AlpacaAPI getAlpacaApi() {
        return alpacaApi;
    }

    public List<Assets> getAllUsEquityAssets() {
        try {
            return alpacaApi.trader().assets().getV2Assets("active", "us_equity", null, null);
        } catch (net.jacobpeterson.alpaca.openapi.trader.ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }

    public void stockExists(String symbol) throws StockNotFoundException  {
        
            List<Assets> assets = getAllUsEquityAssets();
            boolean exists = assets != null && assets.stream().anyMatch(asset -> asset.getSymbol().equals(symbol));
            if (!exists) {
                throw new StockNotFoundException("Stock with symbol '" + symbol + "' does not exist.");
            }
    }

}
