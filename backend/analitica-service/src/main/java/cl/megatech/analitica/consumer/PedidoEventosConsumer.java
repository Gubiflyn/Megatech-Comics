package cl.megatech.analitica.consumer;

import cl.megatech.analitica.dto.PedidoEvento;
import cl.megatech.analitica.service.AnaliticaVentasService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PedidoEventosConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PedidoEventosConsumer.class);

    private final JsonMapper jsonMapper;
    private final AnaliticaVentasService analiticaVentasService;

    public PedidoEventosConsumer(
            JsonMapper jsonMapper,
            AnaliticaVentasService analiticaVentasService) {

        this.jsonMapper = jsonMapper;
        this.analiticaVentasService = analiticaVentasService;
    }

    @KafkaListener(
            topics = "pedidos.eventos",
            groupId = "analitica-ventas"
    )
    public void consumir(ConsumerRecord<String, String> record) {

        LOGGER.info(
                "[KAFKA PEDIDOS] topic={} | particion={} | offset={} | key={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key()
        );

        PedidoEvento evento;

        try {
            evento = jsonMapper.readValue(
                    record.value(),
                    PedidoEvento.class
            );
        } catch (JacksonException ex) {
            throw new IllegalArgumentException(
                    "JSON invalido en pedidos.eventos",
                    ex
            );
        }

        analiticaVentasService.procesar(evento);
    }
}
