package co.edu.unbosque.ms_trading.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TradeMessage {

    private String symbol;
    private double price;
    private int size;
    private String timestamp;
    
}
