package cl.megatech.pedidos.messaging;

import cl.megatech.pedidos.config.PedidoRabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PedidoCreadoProducer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    PedidoCreadoProducer.class
            );

    private final RabbitTemplate rabbitTemplate;

    public PedidoCreadoProducer(
            RabbitTemplate rabbitTemplate) {

        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(
            PedidoCreadoEvent evento) {

        rabbitTemplate.convertAndSend(
                PedidoRabbitConfig.PEDIDO_EXCHANGE,
                PedidoRabbitConfig.PEDIDO_CREADO_ROUTING_KEY,
                evento
        );

        LOGGER.info(
                "Evento pedido.creado publicado. pedidoId={}",
                evento.getPedidoId()
        );
    }
}