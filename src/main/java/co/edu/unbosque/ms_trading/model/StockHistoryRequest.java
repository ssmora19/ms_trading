package co.edu.unbosque.ms_trading.model;

import lombok.Data;

@Data
public class StockHistoryRequest {
    
    private String symbol;
    private String tipo;
    private int unidad;
    private String tiempo;
}
