package cl.megatech.inventario.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// La topología debe coincidir exactamente con StockRabbitConfig de catalogo-service
// (mismos nombres y mismos argumentos), o RabbitMQ responde PRECONDITION_FAILED.
@Configuration
public class StockRabbitConfig {

    public static final String STOCK_EXCHANGE = "inventario.topic";

    public static final String STOCK_ROUTING_KEY = "inventario.stock.actualizado";

    public static final String STOCK_BINDING_KEY = "inventario.stock.*";

    public static final String STOCK_QUEUE = "stock.actualizado.queue";

    public static final String STOCK_DLX = "inventario.dlx";

    public static final String STOCK_DLQ = "stock.actualizado.dlq";

    public static final String STOCK_DLQ_ROUTING_KEY = "inventario.stock.actualizado.dead";

    @Bean
    public MessageConverter stockMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(STOCK_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange stockDeadLetterExchange() {
        return new DirectExchange(STOCK_DLX, true, false);
    }

    @Bean
    public Queue stockActualizadoQueue() {
        return QueueBuilder
                .durable(STOCK_QUEUE)
                .withArgument("x-dead-letter-exchange", STOCK_DLX)
                .withArgument("x-dead-letter-routing-key", STOCK_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue stockActualizadoDlq() {
        return QueueBuilder
                .durable(STOCK_DLQ)
                .build();
    }

    @Bean
    public Binding stockActualizadoBinding(
            Queue stockActualizadoQueue,
            TopicExchange stockExchange) {

        return BindingBuilder
                .bind(stockActualizadoQueue)
                .to(stockExchange)
                .with(STOCK_BINDING_KEY);
    }

    @Bean
    public Binding stockActualizadoDlqBinding(
            Queue stockActualizadoDlq,
            DirectExchange stockDeadLetterExchange) {

        return BindingBuilder
                .bind(stockActualizadoDlq)
                .to(stockDeadLetterExchange)
                .with(STOCK_DLQ_ROUTING_KEY);
    }

}
