package co.edu.unbosque.ms_trading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.ms_trading.component.Market;
import co.edu.unbosque.ms_trading.excepcion.StockNotFoundException;
import co.edu.unbosque.ms_trading.model.dto.history.StockHistoryRequest;
import co.edu.unbosque.ms_trading.service.HistoricalMarketService;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MarketController {
    
    @Autowired
    private HistoricalMarketService historicalMarketService;

    @Autowired
    private Market market;


    @PostMapping("/historico")
    public ResponseEntity<List<?>> getHistorico(@RequestBody StockHistoryRequest parametro) {
        return ResponseEntity.ok(historicalMarketService.getHistorico(parametro));
    }

    @PostMapping("/suscribe/{symbol}")
    public ResponseEntity<String> suscribe(@PathVariable String symbol) {
        market.subscribeToSymbol(symbol);
        return ResponseEntity.ok("Se suscribió correctamente al símbolo: " + symbol);
    }

    @PostMapping("/unsuscribe/{symbol}")
    public ResponseEntity<String> unsuscribe(@PathVariable String symbol) {
        market.unsubscribeSymbol(symbol);
        return ResponseEntity.ok("Se desuscribio correctamente al símbolo: " + symbol);
    }
}
