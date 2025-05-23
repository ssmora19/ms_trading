package co.edu.unbosque.ms_trading.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.ms_trading.model.dto.StockResponse;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenDetail;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenRequest;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenResponse;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockLatestBarsResp;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.Assets;
import net.jacobpeterson.alpaca.openapi.trader.model.Order;
import net.jacobpeterson.alpaca.openapi.trader.model.PostOrderRequest;
import net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce;

@Service
public class TraderService {

    @Autowired
    private AlpacaService alpacaService;


    
    /**
     * Crea una orden en el mercado utilizando la solicitud de orden proporcionada.
     *
     * @param orden la solicitud de orden que contiene los detalles de la orden a crear
     * @return la orden creada o lanza una excepción en caso de error
     * @throws ApiException si ocurre un error al comunicarse con la API de Alpaca
     */
    public OrdenResponse crearOrden(OrdenRequest orden) throws ApiException {
        try {
            PostOrderRequest orderRequest = toPostOrderRequest(orden);
            return toOrdenRequest(alpacaService.getAlpacaApi().trader().orders().postOrder(orderRequest));
        } catch (ApiException e) {
            System.err.println("Error al crear la orden: " + e.getResponseBody());
            throw e; // Re-lanzar la excepción para que el controlador pueda manejarla
        } catch (Exception e) {
            System.err.println("Error inesperado al crear la orden: " + e.getMessage());
            throw new RuntimeException("Error inesperado al crear la orden", e);
        }
    }
    /**
     * Obtiene el precio actual de una acción dado su símbolo.
     *
     * @param symbol el símbolo de la acción (por ejemplo, "AAPL")
     * @return el precio actual de la acción o null si no se encuentra
     * @throws ApiException si ocurre un error al comunicarse con la API de Alpaca
     */
    public Double obtenerPrecioAccion(String symbol) throws ApiException {
        try {
            StockLatestBarsResp barsResp = alpacaService.getAlpacaApi().marketData().stock().stockLatestBars(symbol, null, null);
            if (barsResp != null && barsResp.getBars() != null && barsResp.getBars().get(symbol) != null) {
                return barsResp.getBars().get(symbol).getC();
            }
        } catch (net.jacobpeterson.alpaca.openapi.marketdata.ApiException e) {
            System.err.println("Error al obtener el precio de la acción: " + e.getMessage());
        }
        return null;
    }
    /**
     * Convierte un objeto OrdenRequest en un objeto PostOrderRequest.
     *
     * @param orden la solicitud de orden a convertir
     * @return un objeto PostOrderRequest listo para ser enviado a la API de Alpaca
     */
    private PostOrderRequest toPostOrderRequest(OrdenRequest orden) {
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
    private OrdenResponse toOrdenRequest(Order order) {
        return OrdenResponse.builder()
                .symbol(order.getSymbol())
                .side(order.getSide())
                .quantity(order.getQty())
                .type(order.getType())
                .status(order.getStatus())
                .averagePrice(order.getFilledAvgPrice())
                .build();
    }

    

    /**
     * Obtiene una lista de acciones disponibles en el mercado.
     * @return una lista de objetos StockResponse que representan las acciones
     * @throws ApiException si ocurre un error al comunicarse con la API de Alpaca
     */
    public List<StockResponse> getStocks() {
            List<Assets> acciones = alpacaService.getAllUsEquityAssets();
            List<StockResponse> stockResponses = new ArrayList<>();

            // Obtener los símbolos en lotes de 300
            List<String> symbolsList = acciones.stream()
                    .map(Assets::getSymbol)
                    .collect(Collectors.toList());

            int batchSize = 200;
            List<List<String>> batches = new ArrayList<>();
            for (int i = 0; i < symbolsList.size(); i += batchSize) {
                int end = Math.min(i + batchSize, symbolsList.size());
                batches.add(symbolsList.subList(i, end));
            }

            // Ejecutar cada lote en paralelo
            stockResponses = batches.parallelStream()
                .flatMap(batch -> {
                    String symbols = String.join(",", batch);
                    try {
                        StockLatestBarsResp barsResp = alpacaService.getAlpacaApi().marketData().stock().stockLatestBars(symbols, null, null);
                        return batch.stream()
                                .map(symbol -> {
                                    Assets accion = acciones.stream().filter(a -> a.getSymbol().equals(symbol)).findFirst().orElse(null);
                                    if (accion != null && barsResp.getBars().get(symbol) != null) {
                                        String name = accion.getName();
                                        Double price = barsResp.getBars().get(symbol).getC();
                                        return new StockResponse(name, symbol, price);
                                    }
                                    return null;
                                })
                                .filter(sr -> sr != null);
                    } catch (net.jacobpeterson.alpaca.openapi.marketdata.ApiException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());


            // Ordenar la lista por nombre de la acción (puedes cambiar a symbol si prefieres)
            // Usar Collections.sort con un Comparator eficiente para grandes volúmenes
            stockResponses.sort(java.util.Comparator.comparing(StockResponse::getName, String.CASE_INSENSITIVE_ORDER));
            return stockResponses;

    }


    public OrdenDetail obtenerOrden(UUID uuid){

        try {
            Order orden = alpacaService.getAlpacaApi().trader().orders().getOrderByOrderID(uuid, null);
            return toOrdenDetail(orden);

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    private OrdenDetail toOrdenDetail(Order order) {
        return OrdenDetail.builder()
                .id(order.getId())
                .symbol(order.getSymbol())
                .status(order.getStatus().name())
                .orderType(order.getType().name())
                .side(order.getSide().name())
                .qty(order.getQty())
                .limitPrice(order.getLimitPrice())
                .stopPrice(order.getStopPrice())
                .filledAvgPrice(order.getFilledAvgPrice())
                .createdAt(order.getCreatedAt().toString())
                .updateAt(order.getUpdatedAt().toString())
                .filledAt(order.getFilledAt().toString())
                .build();
    }

    public void mostrarPosiciones(){
        try {
            alpacaService.getAlpacaApi().trader().positions().getAllOpenPositions()
                            .forEach(
                                position -> {
                                    // System.out.println("Symbol: " + position.getSymbol());
                                    // System.out.println("Quantity: " + position.getQty());
                                    // System.out.println("Average Entry Price: " + position.getAvgEntryPrice());
                                    System.out.println("Position: " + position);
                                }
                            );
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    // public void mostrarCuentas() throws ApiException {
    // String cuenta =
    // alpacaService.getTraderAPI().accounts().getAccount().getAccountNumber();
    // System.out.println("Cuenta: " + cuenta);
    // Account a =
    // alpacaService.getTraderAPI().accounts().getAccount().accountNumber(cuenta);
    // System.out.println("Account: " + a);
    // }


}
