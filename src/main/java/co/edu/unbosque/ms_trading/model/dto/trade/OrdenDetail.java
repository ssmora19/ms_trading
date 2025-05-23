package co.edu.unbosque.ms_trading.model.dto.trade;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdenDetail {

    private String id;
    private String symbol;
    private String status;
    private String orderType;
    private String side;
    private String qty;
    private String limitPrice;
    private String stopPrice;
    private String filledAvgPrice;
    private String createdAt;
    private String updateAt;
    private String filledAt;

}
