package co.edu.unbosque.ms_trading.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class BarMessage {

    private String symbol;
    private String time;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

}
