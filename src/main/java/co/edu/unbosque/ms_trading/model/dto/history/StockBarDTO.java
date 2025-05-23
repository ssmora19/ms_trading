package co.edu.unbosque.ms_trading.model.dto.history;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockBarDTO {
    private String timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private long tradeCount;
    private double vwap;
}
