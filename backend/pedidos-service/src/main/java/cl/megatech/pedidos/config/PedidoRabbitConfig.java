package cl.megatech.pedidos.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoRabbitConfig {

    public static final String PEDIDO_EXCHANGE =
            "pedidos.exchange";

    public static final String PEDIDO_CREADO_ROUTING_KEY =
            "pedido.creado";

    @Bean
    public DirectExchange pedidoExchange() {

        return new DirectExchange(
                PEDIDO_EXCHANGE,
                true,
                false
        );
    }
}