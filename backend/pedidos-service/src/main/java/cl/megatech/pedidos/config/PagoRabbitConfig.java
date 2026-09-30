package cl.megatech.pedidos.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagoRabbitConfig {

    // Exchange principal
    public static final String PAGO_EXCHANGE =
            "pagos.exchange";

    // Cola principal
    public static final String PAGO_PROCESADO_QUEUE =
            "pago.procesado.queue";

    // Routing Key principal
    public static final String PAGO_PROCESADO_ROUTING_KEY =
            "pago.procesado";

    // Dead Letter Exchange
    public static final String PAGO_DLX =
            "pagos.dlx";

    // Dead Letter Queue
    public static final String PAGO_PROCESADO_DLQ =
            "pago.procesado.dlq";

    // Routing Key de mensajes fallidos
    public static final String PAGO_PROCESADO_DLQ_ROUTING_KEY =
            "pago.procesado.dead";


    // Conversor JSON para este flujo
    @Bean
    public JacksonJsonMessageConverter pagoJsonMessageConverter() {

        return new JacksonJsonMessageConverter();
    }


    // Configuración específica del consumidor de pagos
    @Bean(name = "pagoRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory
    pagoRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("pagoJsonMessageConverter")
            JacksonJsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(messageConverter);

        // ACK manual
        factory.setAcknowledgeMode(
                AcknowledgeMode.MANUAL
        );

        // Procesar un mensaje a la vez
        factory.setPrefetchCount(1);

        // Si RabbitMQ rechaza un mensaje,
        // no volverlo a poner infinitamente en la cola
        factory.setDefaultRequeueRejected(false);

        return factory;
    }

    // Exchange principal
    @Bean
    public DirectExchange pagoExchange() {

        return new DirectExchange(
                PAGO_EXCHANGE,
                true,
                false
        );
    }

    // Dead Letter Exchange
    @Bean
    public DirectExchange pagoDeadLetterExchange() {

        return new DirectExchange(
                PAGO_DLX,
                true,
                false
        );
    }

    // Cola principal
    @Bean
    public Queue pagoProcesadoQueue() {

        return QueueBuilder
                .durable(PAGO_PROCESADO_QUEUE)

                .withArgument(
                        "x-dead-letter-exchange",
                        PAGO_DLX
                )

                .withArgument(
                        "x-dead-letter-routing-key",
                        PAGO_PROCESADO_DLQ_ROUTING_KEY
                )

                .build();
    }


    // Dead Letter Queue
    @Bean
    public Queue pagoProcesadoDlq() {

        return QueueBuilder
                .durable(PAGO_PROCESADO_DLQ)
                .build();
    }


    // Binding principal
    @Bean
    public Binding pagoProcesadoBinding(
            Queue pagoProcesadoQueue,
            DirectExchange pagoExchange) {

        return BindingBuilder
                .bind(pagoProcesadoQueue)
                .to(pagoExchange)
                .with(PAGO_PROCESADO_ROUTING_KEY);
    }


    // Binding DLQ
    @Bean
    public Binding pagoProcesadoDlqBinding(
            Queue pagoProcesadoDlq,
            DirectExchange pagoDeadLetterExchange) {

        return BindingBuilder
                .bind(pagoProcesadoDlq)
                .to(pagoDeadLetterExchange)
                .with(PAGO_PROCESADO_DLQ_ROUTING_KEY);
    }
}