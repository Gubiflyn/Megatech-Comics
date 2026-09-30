package cl.megatech.pagos.messaging;

import cl.megatech.pagos.config.PagoRabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PagoProcesadoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PagoProcesadoPublisher(
            RabbitTemplate rabbitTemplate) {

        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(
            PagoProcesadoEvent evento) {

        rabbitTemplate.convertAndSend(
                PagoRabbitConfig.PAGO_EXCHANGE,
                PagoRabbitConfig.PAGO_PROCESADO_ROUTING_KEY,
                evento
        );
    }
}