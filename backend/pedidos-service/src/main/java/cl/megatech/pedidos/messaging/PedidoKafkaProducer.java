package cl.megatech.pedidos.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PedidoKafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoKafkaProducer.class);
    private static final String TOPIC = "pedidos.eventos";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PedidoKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(PedidoKafkaEvent evento) {
        try {
            kafkaTemplate.send(TOPIC, evento.pedidoId().toString(), evento)
                    .whenComplete((resultado, error) -> {
                        if (error != null) {
                            registrarError(evento, error);
                            return;
                        }

                        var metadata = resultado.getRecordMetadata();
                        LOGGER.info("[KAFKA] tipo={} eventoId={} topico={} particion={} offset={}",
                                evento.tipo(), evento.eventoId(), metadata.topic(),
                                metadata.partition(), metadata.offset());
                    });
        } catch (RuntimeException ex) {
            registrarError(evento, ex);
        }
    }

    private void registrarError(PedidoKafkaEvent evento, Throwable error) {
        LOGGER.error("[KAFKA] Falló publicación tipo={} eventoId={} topico={}",
                evento.tipo(), evento.eventoId(), TOPIC, error);
    }
}
