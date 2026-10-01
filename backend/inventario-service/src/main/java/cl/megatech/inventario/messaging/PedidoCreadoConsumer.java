package cl.megatech.inventario.messaging;

import cl.megatech.inventario.config.PedidoRabbitConfig;
import cl.megatech.inventario.service.InventarioService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PedidoCreadoConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    PedidoCreadoConsumer.class
            );

    private final InventarioService inventarioService;

    public PedidoCreadoConsumer(
            InventarioService inventarioService) {

        this.inventarioService = inventarioService;
    }

    @RabbitListener(
            queues = PedidoRabbitConfig.PEDIDO_CREADO_QUEUE,
            containerFactory = "pedidoRabbitListenerContainerFactory"
    )
    public void consumir(
            PedidoCreadoEvent evento,
            Message message,
            Channel channel) throws IOException {

        long deliveryTag =
                message
                        .getMessageProperties()
                        .getDeliveryTag();

        try {

            LOGGER.info(
                    "Mensaje pedido.creado recibido. "
                            + "pedidoId={}, usuarioId={}",
                    evento.getPedidoId(),
                    evento.getUsuarioId()
            );

            if (evento.getPedidoId() == null) {
                throw new IllegalArgumentException(
                        "El pedidoId es obligatorio"
                );
            }

            if (evento.getItems() == null
                    || evento.getItems().isEmpty()) {

                throw new IllegalArgumentException(
                        "El pedido no contiene productos"
                );
            }

            Map<Long, Integer> productos =
                    new LinkedHashMap<>();

            for (PedidoCreadoEvent.ItemPedidoEvent item
                    : evento.getItems()) {

                if (item.getProductoId() == null
                        || item.getCantidad() == null
                        || item.getCantidad() <= 0) {

                    throw new IllegalArgumentException(
                            "El pedido contiene un item inválido"
                    );
                }

                productos.merge(
                        item.getProductoId(),
                        item.getCantidad(),
                        Integer::sum
                );
            }

            inventarioService.descontarStockPedido(
                    productos
            );

            channel.basicAck(
                    deliveryTag,
                    false
            );

            LOGGER.info(
                    "ACK enviado. Stock actualizado "
                            + "correctamente para pedido {}.",
                    evento.getPedidoId()
            );

        } catch (Exception ex) {

            LOGGER.error(
                    "Error procesando pedido.creado "
                            + "para pedido {}. "
                            + "El mensaje será enviado a la DLQ.",
                    evento.getPedidoId(),
                    ex
            );

            channel.basicNack(
                    deliveryTag,
                    false,
                    false
            );
        }
    }
}