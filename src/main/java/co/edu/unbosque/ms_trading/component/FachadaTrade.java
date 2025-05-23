package co.edu.unbosque.ms_trading.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.unbosque.ms_trading.excepcion.InsufficientBalanceException;
import co.edu.unbosque.ms_trading.model.dto.StockResponse;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenRequest;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenResponse;
import co.edu.unbosque.ms_trading.service.ApiService;
import co.edu.unbosque.ms_trading.service.TraderService;
import net.jacobpeterson.alpaca.openapi.broker.model.OrderSide;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;

@Component
public class FachadaTrade {

    @Autowired
    private TraderService traderService;

    @Autowired
    private ApiService apiService;

    public List<StockResponse> getStocks() {
        return traderService.getStocks();
    }

    public OrdenResponse createOrder(String userId, OrdenRequest orden) {

        OrdenResponse response;
        try {
            String saldoJson = apiService.enviarSaldo(userId);

            System.out.println(saldoJson);
            if (orden.getOperacion().equals(OrderSide.BUY)) {

                double saldo = 0;
                if (saldoJson != null && saldoJson.contains("saldo")) {
                    saldo = Double.parseDouble(
                            saldoJson.replaceAll("[^0-9.]", ""));

                }
                // Extraer solo el número del JSON recibido
                Double stockPrice = traderService.obtenerPrecioAccion(orden.getSymbol());
                boolean verificacion = verifyBalance(saldo, stockPrice * orden.getCantidad());
                if (!verificacion) {
                    throw new InsufficientBalanceException("No hay saldo suficiente para realizar la compra");
                }
                response = traderService.crearOrden(orden);
                Double nuevoSaldo = saldo - (stockPrice * orden.getCantidad());
                apiService.actualizarSaldo(userId, nuevoSaldo);
                return response;
            }




        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metodo que se encarga de verificar la existencia del usuario.
     * 
     * @param userId
     */
    private void verifyUser(String userId) {

    }

    /**
     * Metodo que se encarga de verificar que se posee suficiente saldo para
     * realizar la compra.
     * 
     * @param userId
     */
    private boolean verifyBalance(Double userBalance, Double stockPrice) {
        if (userBalance == null || stockPrice == null) {
            return false;
        }
        return userBalance >= stockPrice;
    }

    // /**
    // * Metodo qeu se encarga de añadir saldo a la cuenta del ususario.
    // * @param userId
    // * @param amount
    // */
    // private void addToBalance(String userId, double amount) {
    // traderService.addToBalance(userId, amount);
    // }

    // private void verifyStock(String useID) {
    // traderService.verifyStock(stockId);
    // }

}
