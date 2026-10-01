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

    public static final String PAGO_EXCHANGE =
            "pagos.exchange";

    public static final String PAGO_PROCESADO_QUEUE =
            "pago.procesado.queue";

    public static final String PAGO_PROCESADO_ROUTING_KEY =
            "pago.procesado";

    public static final String PAGO_DLX =
            "pagos.dlx";

    public static final String PAGO_PROCESADO_DLQ =
            "pago.procesado.dlq";

    public static final String PAGO_PROCESADO_DLQ_ROUTING_KEY =
            "pago.procesado.dead";


    @Bean
    public JacksonJsonMessageConverter pagoJsonMessageConverter() {

        return new JacksonJsonMessageConverter();
    }


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

        factory.setAcknowledgeMode(
                AcknowledgeMode.MANUAL
        );

        factory.setPrefetchCount(1);

        factory.setDefaultRequeueRejected(false);

        return factory;
    }


    @Bean
    public DirectExchange pagoExchange() {

        return new DirectExchange(
                PAGO_EXCHANGE,
                true,
                false
        );
    }


    @Bean
    public DirectExchange pagoDeadLetterExchange() {

        return new DirectExchange(
                PAGO_DLX,
                true,
                false
        );
    }


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


    @Bean
    public Queue pagoProcesadoDlq() {

        return QueueBuilder
                .durable(PAGO_PROCESADO_DLQ)
                .build();
    }


    @Bean
    public Binding pagoProcesadoBinding(
            @Qualifier("pagoProcesadoQueue")
            Queue pagoProcesadoQueue,

            @Qualifier("pagoExchange")
            DirectExchange pagoExchange) {

        return BindingBuilder
                .bind(pagoProcesadoQueue)
                .to(pagoExchange)
                .with(PAGO_PROCESADO_ROUTING_KEY);
    }


    @Bean
    public Binding pagoProcesadoDlqBinding(
            @Qualifier("pagoProcesadoDlq")
            Queue pagoProcesadoDlq,

            @Qualifier("pagoDeadLetterExchange")
            DirectExchange pagoDeadLetterExchange) {

        return BindingBuilder
                .bind(pagoProcesadoDlq)
                .to(pagoDeadLetterExchange)
                .with(PAGO_PROCESADO_DLQ_ROUTING_KEY);
    }
}