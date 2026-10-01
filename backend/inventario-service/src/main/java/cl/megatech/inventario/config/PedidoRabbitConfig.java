package cl.megatech.inventario.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoRabbitConfig {

    // Exchange principal
    public static final String PEDIDO_EXCHANGE =
            "pedidos.exchange";

    // Cola principal
    public static final String PEDIDO_CREADO_QUEUE =
            "pedido.creado.queue";

    // Routing key principal
    public static final String PEDIDO_CREADO_ROUTING_KEY =
            "pedido.creado";

    // Dead Letter Exchange
    public static final String PEDIDO_DLX =
            "pedidos.dlx";

    // Dead Letter Queue
    public static final String PEDIDO_CREADO_DLQ =
            "pedido.creado.dlq";

    // Routing key para mensajes fallidos
    public static final String PEDIDO_CREADO_DLQ_ROUTING_KEY =
            "pedido.creado.dead";

    @Bean(name = "pedidoRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory
    pedidoRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(
                new JacksonJsonMessageConverter()
        );

        // ACK manual
        factory.setAcknowledgeMode(
                AcknowledgeMode.MANUAL
        );

        // Un mensaje a la vez
        factory.setPrefetchCount(1);

        // Un mensaje rechazado no vuelve infinitamente
        // a la cola principal.
        factory.setDefaultRequeueRejected(false);

        return factory;
    }

    @Bean
    public DirectExchange pedidoExchange() {
        return new DirectExchange(
                PEDIDO_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public DirectExchange pedidoDeadLetterExchange() {
        return new DirectExchange(
                PEDIDO_DLX,
                true,
                false
        );
    }

    @Bean
    public Queue pedidoCreadoQueue() {

        return QueueBuilder
                .durable(PEDIDO_CREADO_QUEUE)
                .withArgument(
                        "x-dead-letter-exchange",
                        PEDIDO_DLX
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        PEDIDO_CREADO_DLQ_ROUTING_KEY
                )
                .build();
    }

    @Bean
    public Queue pedidoCreadoDlq() {

        return QueueBuilder
                .durable(PEDIDO_CREADO_DLQ)
                .build();
    }

    @Bean
    public Binding pedidoCreadoBinding(
            Queue pedidoCreadoQueue,
            DirectExchange pedidoExchange) {

        return BindingBuilder
                .bind(pedidoCreadoQueue)
                .to(pedidoExchange)
                .with(PEDIDO_CREADO_ROUTING_KEY);
    }

    @Bean
    public Binding pedidoCreadoDlqBinding(
            Queue pedidoCreadoDlq,
            DirectExchange pedidoDeadLetterExchange) {

        return BindingBuilder
                .bind(pedidoCreadoDlq)
                .to(pedidoDeadLetterExchange)
                .with(PEDIDO_CREADO_DLQ_ROUTING_KEY);
    }
}