package co.edu.unbosque.ms_trading.model.dto.history;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockLineDTO {

    private String time;
    private double close;
}
