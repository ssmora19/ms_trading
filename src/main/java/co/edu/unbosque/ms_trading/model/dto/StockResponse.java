package co.edu.unbosque.ms_trading.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockResponse {

    private String name;
    private String symbol;
    private Double price;
}