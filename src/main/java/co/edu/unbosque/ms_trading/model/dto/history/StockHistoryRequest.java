package co.edu.unbosque.ms_trading.model.dto.history;

import lombok.Data;

@Data
public class StockHistoryRequest {
    
    private String symbol;
    private String tipo;
    private int unidad;
    private String tiempo;
}
