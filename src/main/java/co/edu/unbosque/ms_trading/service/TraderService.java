package co.edu.unbosque.ms_trading.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.ms_trading.model.OrdenRequest;
import co.edu.unbosque.ms_trading.model.OrdenResponse;
import net.jacobpeterson.alpaca.openapi.broker.model.OrderType;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.Assets;
import net.jacobpeterson.alpaca.openapi.trader.model.Order;
import net.jacobpeterson.alpaca.openapi.trader.model.PostOrderRequest;
import net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce;

@Service
public class TraderService {

    @Autowired
    private AlpacaService alpacaService;


    public void mostrarPosiciones(){
        try {
            alpacaService.getAlpacaApi().trader().positions().getAllOpenPositions()
                            .forEach(
                                position -> {
                                    System.out.println("Symbol: " + position.getSymbol());
                                    System.out.println("Quantity: " + position.getQty());
                                    System.out.println("Average Entry Price: " + position.getAvgEntryPrice());
                                }
                            );
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * Crea una orden en el mercado utilizando la solicitud de orden proporcionada.
     *
     * @param orden la solicitud de orden que contiene los detalles de la orden a crear
     * @return la orden creada o lanza una excepción en caso de error
     * @throws ApiException si ocurre un error al comunicarse con la API de Alpaca
     */
    public Order crearOrden(OrdenRequest orden) throws ApiException {
        try {
            PostOrderRequest orderRequest = toPostOrderRequest(orden);
            return alpacaService.getAlpacaApi().trader().orders().postOrder(orderRequest);
        } catch (ApiException e) {
            System.err.println("Error al crear la orden: " + e.getResponseBody());
            throw e; // Re-lanzar la excepción para que el controlador pueda manejarla
        } catch (Exception e) {
            System.err.println("Error inesperado al crear la orden: " + e.getMessage());
            throw new RuntimeException("Error inesperado al crear la orden", e);
        }
    }

    /**
     * Convierte un objeto OrdenRequest en un objeto PostOrderRequest.
     *
     * @param orden la solicitud de orden a convertir
     * @return un objeto PostOrderRequest listo para ser enviado a la API de Alpaca
     */
    public PostOrderRequest toPostOrderRequest(OrdenRequest orden) {
        PostOrderRequest request = new PostOrderRequest()
                .symbol(orden.getSymbol())
                .qty(orden.getCantidad().toString())
                .side(orden.getOperacion())
                .type(orden.getTipoOrden())
                .timeInForce(TimeInForce.DAY); // puedes parametrizar esto si quieres

        switch (orden.getTipoOrden().name()) {
            case "LIMIT":

                request.setLimitPrice(orden.getLimitPrice());
                break;
            case "STOP":
                request.setStopPrice(orden.getStopPrice());
                break;
            case "STOP_LIMIT":
                request.setLimitPrice(orden.getLimitPrice());
                request.setStopPrice(orden.getStopPrice());
                break;
            case "MARKET":
                break;
            default:
                throw new IllegalArgumentException("Tipo de orden no soportado: " + orden.getTipoOrden());
        }

        return request;
    }

    /**
     * Convierte un objeto Order en un objeto OrdenRequest.
     *
     * @param order el objeto de orden a convertir
     * @return un objeto OrdenRequest que contiene los detalles de la orden
     */
    public OrdenResponse toOrdenRequest(Order order) {
        return OrdenResponse.builder()
                .symbol(order.getSymbol())
                .side(order.getSide())
                .quantity(order.getQty())
                .type(order.getType())
                .status(order.getStatus())
                .filledQuantity(order.getFilledQty())
                .averagePrice(order.getFilledAvgPrice())
                .build();
    }

    public void mostrarAcciones() {
        try {
            List<Assets> acciones = alpacaService.getAlpacaApi().trader().assets().getV2Assets("active", "us_equity",
                    null, null);
            for (Assets accion : acciones) {
                System.out.println("Symbol: " + accion.getSymbol() + ", Name: " + accion.getName());
            }
            System.out.println(acciones.get(acciones.size() - 1));
            System.out.println("Total de acciones: " + acciones.size());

            Set<String> mercados = acciones.stream().map(Assets -> Assets.getExchange().name())
                    .collect(Collectors.toSet());

            System.out.println("Exchanges disponibles: " + mercados);

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // public Order crearOrden(String symbol, String side, int cantidad) throws
    // ApiException {
    // Order order = new Order();
    // order.setSymbol(symbol);
    // order.setSide(side);
    // order.setType("market");
    // order.setQuantity(cantidad);
    // order.setTimeInForce("gtc");
    // return alpacaService.getTraderAPI().orders().createOrder(order);
    // }

    // public void mostrarCuentas() throws ApiException {
    // String cuenta =
    // alpacaService.getTraderAPI().accounts().getAccount().getAccountNumber();
    // System.out.println("Cuenta: " + cuenta);
    // Account a =
    // alpacaService.getTraderAPI().accounts().getAccount().accountNumber(cuenta);
    // System.out.println("Account: " + a);
    // }

    // public void informacionFinanciera() throws ApiException, InterruptedException
    // {
    // AlpacaAPI alp = alpacaService.getAlpacaApi();

    // alp.stockMarketDataStream().connect();
    // if(!alp.stockMarketDataStream().waitForAuthorization(5, TimeUnit.SECONDS)) {
    // throw new RuntimeException("No se pudo conectar al stream de datos de mercado
    // de Alpaca.");
    // }

    // alp.stockMarketDataStream().setListener(new StockMarketDataListenerAdapter(){
    // @Override
    // public void onTrade(StockTradeMessage trade) {
    // System.out.println("Trade recibidio: "+ trade);
    // }

    // });

    // alp.stockMarketDataStream().setTradeSubscriptions(Set.of("AAPL"));
    // System.out.println("suscripcion a las acciones de AAPL");

    // Thread.sleep(5000);

    // alp.stockMarketDataStream().disconnect();
    // System.out.println("Desconectado del stream de datos de mercado de Alpaca.");

    // }

}
