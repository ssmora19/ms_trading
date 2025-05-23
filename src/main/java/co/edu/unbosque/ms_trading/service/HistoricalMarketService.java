package co.edu.unbosque.ms_trading.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.ms_trading.excepcion.StockNotFoundException;
import co.edu.unbosque.ms_trading.model.dto.history.StockBarDTO;
import co.edu.unbosque.ms_trading.model.dto.history.StockHistoryRequest;
import co.edu.unbosque.ms_trading.model.dto.history.StockLineDTO;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBarsResp;

@Service
public class HistoricalMarketService {

    @Autowired
    AlpacaService alpacaService;

    public List<?> getHistorico(StockHistoryRequest parametro) throws StockNotFoundException {

        String simbolo = parametro.getSymbol();
        String tiempo = parametro.getUnidad() + parametro.getTiempo();
        // alpacaService.stockExists(simbolo);
        if (parametro.getTipo().equals("bar")) {
            return historicoBarras(simbolo, tiempo);

        } else if (parametro.getTipo().equals("line")) {
            return historicoLineal(simbolo, tiempo);

        }
        return null;
    }

    private List<StockBarDTO> historicoBarras(String simbolo, String tiempo) {
        try {
            StockBarsResp barResp = alpacaService.getAlpacaApi().marketData().stock().stockBars(simbolo, tiempo, OffsetDateTime.now(ZoneOffset.UTC).minusDays(5),
                    null, null, null, null, null, null, null, null);

            List<StockBarDTO> datos = barResp.getBars().values().stream().flatMap(List::stream)
                    .map(this::converrStockBar)
                    .collect(Collectors.toList());
            return datos;

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public void datosHistoricos(){
            StockBarsResp barResp;
            try {
                barResp = alpacaService.getAlpacaApi().marketData().stock().stockBars("TSLA", "1Min", OffsetDateTime.now(ZoneOffset.UTC).minusDays(5),
                        null, null, null, null, null, null, null, null);
                        System.out.println(barResp);

            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



    }

    private List<StockLineDTO> historicoLineal(String simbolo, String tiempo) {
        try {

            StockBarsResp barResp = alpacaService.getAlpacaApi().marketData().stock().stockBars(simbolo, tiempo, OffsetDateTime.now(ZoneOffset.UTC).minusDays(5),
                    null, null, null, null, null, null, null, null);
            System.out.println(barResp);

            List<StockLineDTO> datos = barResp.getBars().values().stream().flatMap(List::stream)
                    .map(this::convertStockLine)
                    .collect(Collectors.toList());
            return datos;

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    private StockLineDTO convertStockLine(StockBar stockTrade) {
        return new StockLineDTO(stockTrade.getT().toString(), stockTrade.getC());
    }

    private StockBarDTO converrStockBar(StockBar stockBar) {
        return StockBarDTO.builder()
                .timestamp(stockBar.getT().toString())
                .open(stockBar.getO())
                .high(stockBar.getH())
                .low(stockBar.getL())
                .close(stockBar.getC())
                .volume(stockBar.getV())
                .tradeCount(stockBar.getN())
                .vwap(stockBar.getVw())
                .build();
    }
}
