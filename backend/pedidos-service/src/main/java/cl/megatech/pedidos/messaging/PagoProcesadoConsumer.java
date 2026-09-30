package cl.megatech.pedidos.messaging;

import cl.megatech.pedidos.config.PagoRabbitConfig;
import cl.megatech.pedidos.service.PedidoService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PagoProcesadoConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    PagoProcesadoConsumer.class
            );

    private final PedidoService pedidoService;

    public PagoProcesadoConsumer(
            PedidoService pedidoService) {

        this.pedidoService = pedidoService;
    }

    @RabbitListener(
            queues = PagoRabbitConfig.PAGO_PROCESADO_QUEUE,
            containerFactory = "pagoRabbitListenerContainerFactory"
    )
    public void consumir(
            PagoProcesadoEvent evento,
            Message message,
            Channel channel) throws IOException {

        long deliveryTag =
                message
                        .getMessageProperties()
                        .getDeliveryTag();

        try {

            LOGGER.info(
                    "Mensaje pago.procesado recibido. "
                            + "pagoId={}, pedidoId={}, estado={}",
                    evento.getPagoId(),
                    evento.getPedidoId(),
                    evento.getEstado()
            );

            // Seguridad adicional:
            // solamente un pago aprobado puede
            // marcar un pedido como PAGADO.
            if (!"APROBADO".equalsIgnoreCase(
                    evento.getEstado())) {

                throw new IllegalArgumentException(
                        "El evento recibido no corresponde "
                                + "a un pago aprobado"
                );
            }

            // Actualizar el pedido
            pedidoService.actualizarEstado(
                    evento.getPedidoId(),
                    "PAGADO"
            );

            // ACK
            // El mensaje se procesó correctamente
            channel.basicAck(
                    deliveryTag,
                    false
            );

            LOGGER.info(
                    "ACK enviado. Pedido {} actualizado a PAGADO.",
                    evento.getPedidoId()
            );

        } catch (Exception ex) {

            LOGGER.error(
                    "Error procesando pago.procesado "
                            + "para pedido {}. "
                            + "El mensaje será enviado a la DLQ.",
                    evento.getPedidoId(),
                    ex
            );

            // NACK
            // requeue = false
            //
            // El mensaje no vuelve a la cola principal.
            // RabbitMQ lo enviará al DLX y luego a la DLQ.
            channel.basicNack(
                    deliveryTag,
                    false,
                    false
            );
        }
    }
}