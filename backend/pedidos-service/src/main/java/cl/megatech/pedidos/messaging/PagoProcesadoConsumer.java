package cl.megatech.pedidos.messaging;

import cl.megatech.pedidos.config.PagoRabbitConfig;
import cl.megatech.pedidos.exception.RecursoNoEncontradoException;
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

    private static final int MAX_REINTENTOS = 3;

    private final PedidoService pedidoService;

    public PagoProcesadoConsumer(
            PedidoService pedidoService) {

        this.pedidoService = pedidoService;
    }

    @RabbitListener(
        id = "pago-procesado-listener",
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

            validarEvento(evento);

            procesarConReintentos(evento);

            channel.basicAck(
                    deliveryTag,
                    false
            );

            LOGGER.info(
                    "ACK enviado. Pedido {} actualizado a PAGADO.",
                    evento.getPedidoId()
            );

        } catch (RecursoNoEncontradoException
                 | IllegalArgumentException ex) {

            LOGGER.error(
                    "Error no recuperable procesando pago.procesado "
                            + "para pedido {}. "
                            + "El mensaje será enviado directamente "
                            + "a la DLQ.",
                    evento != null
                            ? evento.getPedidoId()
                            : null,
                    ex
            );

            channel.basicNack(
                    deliveryTag,
                    false,
                    false
            );

        } catch (Exception ex) {

            LOGGER.error(
                    "El mensaje pago.procesado no pudo procesarse "
                            + "después de {} intentos para pedido {}. "
                            + "Será enviado a la DLQ.",
                    MAX_REINTENTOS,
                    evento != null
                            ? evento.getPedidoId()
                            : null,
                    ex
            );

            channel.basicNack(
                    deliveryTag,
                    false,
                    false
            );
        }
    }

    private void validarEvento(
            PagoProcesadoEvent evento) {

        if (evento == null) {

            throw new IllegalArgumentException(
                    "El evento pago.procesado no puede ser nulo"
            );
        }

        if (evento.getPagoId() == null) {

            throw new IllegalArgumentException(
                    "El pagoId es obligatorio"
            );
        }

        if (evento.getPedidoId() == null) {

            throw new IllegalArgumentException(
                    "El pedidoId es obligatorio"
            );
        }

        if (evento.getEstado() == null
                || evento.getEstado().isBlank()) {

            throw new IllegalArgumentException(
                    "El estado del pago es obligatorio"
            );
        }

        if (!"APROBADO".equalsIgnoreCase(
                evento.getEstado())) {

            throw new IllegalArgumentException(
                    "El evento recibido no corresponde "
                            + "a un pago aprobado"
            );
        }
    }

    private void procesarConReintentos(
            PagoProcesadoEvent evento) {

        RuntimeException ultimoError = null;

        for (int intento = 1;
             intento <= MAX_REINTENTOS;
             intento++) {

            try {

                LOGGER.info(
                        "Procesando pago.procesado. "
                                + "pagoId={}, pedidoId={}, "
                                + "intento={}/{}",
                        evento.getPagoId(),
                        evento.getPedidoId(),
                        intento,
                        MAX_REINTENTOS
                );

                pedidoService.actualizarEstado(
                        evento.getPedidoId(),
                        "PAGADO"
                );

                return;

            } catch (RecursoNoEncontradoException
                     | IllegalArgumentException ex) {

                throw ex;

            } catch (RuntimeException ex) {

                ultimoError = ex;

                LOGGER.warn(
                        "Error temporal procesando pedido {}. "
                                + "Intento {}/{} fallido.",
                        evento.getPedidoId(),
                        intento,
                        MAX_REINTENTOS,
                        ex
                );
            }
        }

        throw ultimoError != null
                ? ultimoError
                : new IllegalStateException(
                        "No fue posible procesar "
                                + "el evento pago.procesado"
                );
    }
}