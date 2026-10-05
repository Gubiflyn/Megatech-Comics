package cl.megatech.inventario.messaging;

import cl.megatech.inventario.config.StockRabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class StockActualizadoProducer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    StockActualizadoProducer.class
            );

    private final RabbitTemplate rabbitTemplate;

    public StockActualizadoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(StockActualizadoEvent evento) {
        try {
            rabbitTemplate.convertAndSend(
                    StockRabbitConfig.STOCK_EXCHANGE,
                    StockRabbitConfig.STOCK_ROUTING_KEY,
                    evento
            );

            LOGGER.info(
                    "[RABBITMQ STOCK] Evento stock.actualizado publicado.\n"
                            + "productoId={} stockAnterior={} stockActual={} routingKey={}",
                    evento.getProductoId(),
                    evento.getStockAnterior(),
                    evento.getStockActual(),
                    StockRabbitConfig.STOCK_ROUTING_KEY
            );

        } catch (Exception ex) {

            LOGGER.error(
                    "[RABBITMQ STOCK] No se pudo publicar stock.actualizado "
                            + "para productoId={}. El descuento de stock no se revierte.",
                    evento.getProductoId(),
                    ex
            );
        }
    }
}
