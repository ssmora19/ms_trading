package co.edu.unbosque.ms_trading;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import co.edu.unbosque.ms_trading.component.Market;
import co.edu.unbosque.ms_trading.component.MarketSimulator;
import co.edu.unbosque.ms_trading.model.dto.trade.OrdenRequest;
import co.edu.unbosque.ms_trading.service.AlpacaService;
import co.edu.unbosque.ms_trading.service.ApiService;
import co.edu.unbosque.ms_trading.service.HistoricalMarketService;
import co.edu.unbosque.ms_trading.service.TraderService;
import net.jacobpeterson.alpaca.openapi.trader.model.Order;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderSide;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderType;

@SpringBootApplication
public class MsTradingApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MsTradingApplication.class, args);
	}

	@Autowired
	private TraderService trader;

    @Autowired
    private Market market;

    @Autowired
    private AlpacaService alpacaService;

    @Autowired
    private MarketSimulator marketSimulator;

    @Autowired
    private ApiService apiService;
    
    @Autowired
    private HistoricalMarketService historicalMarketService;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;



	@Override
    public void run(String... args) throws Exception {

        // Double valor = trader.obtenerPrecioAccion("AAPL");
        // System.out.println("El valor de la acción es: " + valor);
        // apiService.enviarSaldo("kperdomof@unbosque.edu.co---");
        // apiService.actualizarSaldo("kperdomof@unbosque.edu.co", 4500.0);

        // trader.mostrarCuentas(); // <-- ¡Aquí llamas a tu método de prueba!


        // boolean marketOpen = alpacaService.getAlpacaApi().trader().clock().getClock().getIsOpen();
        // System.out.println("¿El mercado está abierto? " + marketOpen);

        marketSimulator.startSimulation();
        // trader.mostrarAcciones();
        // trader.getStocks();

        // -------------------      CRERA UNA ORDEN DE COMPRA O VENTA       -------------------

        // OrdenRequest orden = OrdenRequest.builder()
        //         .symbol("TSLA")
        //         .cantidad(1)
        //         .operacion(OrderSide.BUY)
        //         .tipoOrden(OrderType.MARKET)
        //         .stopPrice("335.75")
        //         .build();

        // Order ejecucion = trader.crearOrden(orden);
        // System.out.println("Orden ejecutada: " + ejecucion);

        // -------------------      REVISRAR UNA ORDEN       -------------------

        // UUID uuid = UUID.fromString("3cd6fe76-b9d6-4765-a2b6-1e10cf4f47ab");
        // trader.obtenerOrden(uuid);
        
        // trader.mostrarPosiciones();
        // apiService.getUserData();

        // Scanner scanner = new Scanner(System.in);
        // while (true) {
        //     System.out.println("Opciones: ");
        //     System.out.println("1. Suscribirse a un símbolo");
        //     System.out.println("2. Desuscribirse de un símbolo");
        //     System.out.println("3. Salir");
        //     System.out.print("Selecciona una opción: ");
        //     int opcion = scanner.nextInt();
        //     scanner.nextLine(); // Consumir la nueva línea

        //     switch (opcion) {
        //         case 1:
        //             System.out.print("Introduce el símbolo para suscribirte: ");
        //             String subscribeSymbol = scanner.nextLine();
        //             market.subscribeToSymbol(subscribeSymbol);
        //             break;
        //         case 2:
        //             System.out.print("Introduce el símbolo para desuscribirte: ");
        //             String unsubscribeSymbol = scanner.nextLine();
        //             market.unsubscribeSymbol(unsubscribeSymbol);
        //             break;
        //         case 3:
        //             System.out.println("Saliendo...");
        //             return;
        //         default:
        //             System.out.println("Opción no válida. Inténtalo de nuevo.");
        //     }
        // }
       


        // Si quieres que la aplicación se detenga automáticamente después de ejecutar la prueba,
        // puedes cerrar el contexto de la aplicación.
        // Importa ConfigurableApplicationContext context;
        // @Autowired ConfigurableApplicationContext context;
        // context.close(); // Esto cerrará la aplicación

        // historicalMarketService.datosHistoricos();





        kafkaTemplate.send("trading-topic", "Hola desde Kafka");



    }


}
