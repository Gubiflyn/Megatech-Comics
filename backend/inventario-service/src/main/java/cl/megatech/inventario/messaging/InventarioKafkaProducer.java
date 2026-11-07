package cl.megatech.inventario.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventarioKafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventarioKafkaProducer.class);
    private static final String TOPIC = "inventario.eventos";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventarioKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(StockKafkaEvent evento) {
        try {
            kafkaTemplate.send(TOPIC, evento.productoId().toString(), evento)
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

    private void registrarError(StockKafkaEvent evento, Throwable error) {
        LOGGER.error("[KAFKA] Falló publicación tipo={} eventoId={} topico={}",
                evento.tipo(), evento.eventoId(), TOPIC, error);
    }
}
