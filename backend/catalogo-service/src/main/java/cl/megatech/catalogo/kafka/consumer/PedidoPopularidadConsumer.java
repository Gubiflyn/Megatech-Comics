package cl.megatech.catalogo.kafka.consumer;

import cl.megatech.catalogo.kafka.dto.PedidoPopularidadEvento;
import cl.megatech.catalogo.kafka.service.PopularidadService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PedidoPopularidadConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PedidoPopularidadConsumer.class);

    private final JsonMapper jsonMapper;
    private final PopularidadService popularidadService;

    public PedidoPopularidadConsumer(
            JsonMapper jsonMapper,
            PopularidadService popularidadService) {

        this.jsonMapper = jsonMapper;
        this.popularidadService = popularidadService;
    }

    @KafkaListener(
            topics = "pedidos.eventos",
            groupId = "catalogo-populares"
    )
    public void consumir(ConsumerRecord<String, String> record) {

        LOGGER.info(
                "[CATALOGO KAFKA] topic={} | particion={} | offset={} | key={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key()
        );

        PedidoPopularidadEvento evento;

        try {
            evento = jsonMapper.readValue(
                    record.value(),
                    PedidoPopularidadEvento.class
            );
        } catch (JacksonException ex) {
            throw new IllegalArgumentException(
                    "JSON invalido en pedidos.eventos",
                    ex
            );
        }

        if (evento == null ||
                evento.pedidoId() == null ||
                record.key() == null ||
                !record.key().equals(evento.pedidoId().toString())) {

            throw new IllegalArgumentException(
                    "La clave Kafka debe coincidir con pedidoId"
            );
        }

        popularidadService.procesar(evento);
    }
}
