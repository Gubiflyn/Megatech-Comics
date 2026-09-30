package cl.megatech.pagos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
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

    // Routing Key para mensajes fallidos
    public static final String PAGO_PROCESADO_DLQ_ROUTING_KEY =
            "pago.procesado.dead";


    // Conversor JSON
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }


    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jsonMessageConverter) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(
                jsonMessageConverter
        );

        return rabbitTemplate;
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