package co.edu.unbosque.ms_trading.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderSide;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderType;

@Data
@Builder
@AllArgsConstructor
public class OrdenRequest {
    
    private String symbol;
    private Integer cantidad;
    private OrderSide operacion;
    private OrderType tipoOrden;

    private String limitPrice;
    private String stopPrice; 
    
}
