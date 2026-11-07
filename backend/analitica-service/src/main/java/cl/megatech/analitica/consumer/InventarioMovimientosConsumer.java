package cl.megatech.analitica.consumer;

import cl.megatech.analitica.dto.InventarioMovimientoEvento;
import cl.megatech.analitica.service.AnaliticaStockService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class InventarioMovimientosConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InventarioMovimientosConsumer.class);

    private final JsonMapper jsonMapper;
    private final AnaliticaStockService analiticaStockService;

    public InventarioMovimientosConsumer(
            JsonMapper jsonMapper,
            AnaliticaStockService analiticaStockService) {

        this.jsonMapper = jsonMapper;
        this.analiticaStockService = analiticaStockService;
    }

    @KafkaListener(
        topics = "inventario.eventos",
        groupId = "analitica-stock"
)
    public void consumir(ConsumerRecord<String, String> record) {

        LOGGER.info(
                "[KAFKA INVENTARIO] topic={} | particion={} | offset={} | key={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key()
        );

        InventarioMovimientoEvento evento;

        try {
            evento = jsonMapper.readValue(
                    record.value(),
                    InventarioMovimientoEvento.class
            );
        } catch (JacksonException ex) {
            throw new IllegalArgumentException(
                    "JSON invalido en inventario.eventos",
                    ex
            );
        }

        // La clave del topic compactado debe ser productoId.
        if (evento == null ||
                evento.productoId() == null ||
                record.key() == null ||
                !record.key().equals(evento.productoId().toString())) {

            throw new IllegalArgumentException(
                    "La clave Kafka debe coincidir con productoId"
            );
        }

        analiticaStockService.procesar(evento);
    }
}
