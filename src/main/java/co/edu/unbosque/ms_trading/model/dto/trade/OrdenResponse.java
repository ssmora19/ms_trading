package co.edu.unbosque.ms_trading.model.dto.trade;


import lombok.Builder;
import lombok.Data;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderSide;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderStatus;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderType;

@Data
@Builder
public class OrdenResponse {
    private String symbol;
    private OrderSide side;
    private String quantity;
    private OrderType type;
    private OrderStatus status;
    private String averagePrice;
}
