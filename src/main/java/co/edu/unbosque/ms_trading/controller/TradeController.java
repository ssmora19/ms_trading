package co.edu.unbosque.ms_trading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.ms_trading.component.FachadaTrade;
import co.edu.unbosque.ms_trading.model.dto.StockResponse;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenRequest;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenResponse;
import co.edu.unbosque.ms_trading.service.TraderService;

@RestController
@RequestMapping("/trade")
public class TradeController {
    
    @Autowired
    private FachadaTrade traderService;

    /**
     * Retorna una lista de acciones
     * @return ResponseEntity con una lista de acciones
     */
    @GetMapping("stocks")
    public ResponseEntity<List<StockResponse>> getStocks() {
        return ResponseEntity.ok(traderService.getStocks());
    }

    /**
     * Crea una orden de compra o venta
     * @return ResponseEntity con el resultado de la orden
     */
    @PostMapping("Order/{user-id}")
    public ResponseEntity<OrdenResponse> createOrder(@PathVariable("user-id") String userId, @RequestBody OrdenRequest orden) {
        return ResponseEntity.ok(traderService.createOrder(userId, orden));
    }


    // /**
    //  * Retonrna una lista con el portafolio de acciones
    //  * @return ResponseEntity con el portafolio de acciones
    //  */
    // @GetMapping("Portfolio/{user-id}")
    // public ResponseEntity<String> getPortfolio(@PathVariable("user-id") String userId) {
    //     return ResponseEntity.ok(traderService.getPortfolio());
    // }

    // @GetMapping("order/detail/{order-id}")
    // public ResponseEntity<String> getOrderDetail(@PathVariable("order-id") String orderId) {
    //     return ResponseEntity.ok(traderService.getOrderDetail());
    // }

    // @GetMapping("order/history/{user-id}")
    // public ResponseEntity<String> getOrderHistory(@PathVariable("user-id") String userId) {
    //     return ResponseEntity.ok(traderService.getOrderHistory());
    // }


}
