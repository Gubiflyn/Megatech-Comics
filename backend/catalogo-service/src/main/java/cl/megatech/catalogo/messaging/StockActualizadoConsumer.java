package cl.megatech.catalogo.messaging;

import cl.megatech.catalogo.config.StockRabbitConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StockActualizadoConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    StockActualizadoConsumer.class
            );

    @RabbitListener(queues = StockRabbitConfig.STOCK_QUEUE)
    public void consumir(
            StockActualizadoEvent evento,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {

            validar(evento);

            LOGGER.info(
                    "[RABBITMQ STOCK] catalogo-service recibió "
                            + StockRabbitConfig.STOCK_ROUTING_KEY + "\n"
                            + "Producto ID: {} | Stock anterior: {} | Stock actual: {} | Motivo: {}",
                    evento.getProductoId(),
                    evento.getStockAnterior(),
                    evento.getStockActual(),
                    evento.getMotivo()
            );

            channel.basicAck(deliveryTag, false);

            LOGGER.info("[ACK OK] Evento stock.actualizado procesado");

        } catch (Exception ex) {

            LOGGER.error(
                    "Error procesando stock.actualizado. deliveryTag={} evento={}. "
                            + "El mensaje será enviado a la DLQ.",
                    deliveryTag,
                    evento,
                    ex
            );

            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException nackEx) {
                LOGGER.error(
                        "No se pudo enviar NACK para deliveryTag={}",
                        deliveryTag,
                        nackEx
                );
            }
        }
    }

    private void validar(StockActualizadoEvent evento) {

        if (evento == null) {
            throw new IllegalArgumentException(
                    "El evento stock.actualizado es nulo"
            );
        }

        if (evento.getProductoId() == null) {
            throw new IllegalArgumentException(
                    "El productoId es obligatorio"
            );
        }

        if (evento.getStockActual() == null
                || evento.getStockActual() < 0) {

            throw new IllegalArgumentException(
                    "El stockActual debe ser mayor o igual a 0"
            );
        }

        if (evento.getMotivo() == null
                || evento.getMotivo().isBlank()) {

            throw new IllegalArgumentException(
                    "El motivo es obligatorio"
            );
        }
    }
}
