package cl.megatech.kafkaadmin.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

// Tópicos provisionados al arrancar. Spring Boot los crea mediante el
// KafkaAdmin autoconfigurado (independiente del AdminClient de nuestra API
// REST), por eso basta con exponerlos como beans NewTopic.
//
// En AWS los 4 tópicos ya existen con 3 particiones y 3 réplicas (3 brokers);
// estos beans quedan alineados a esa topología para no generar un mismatch
// cuando el servicio arranca contra el clúster real (si el tópico ya existe,
// Spring Boot no lo recrea ni lo modifica, solo lo detecta).
// Local (infra/kafka, 2 brokers) usa el mismo factor de replicación vía
// KAFKA_TOPIC_REPLICATION_FACTOR; en AWS esa variable se fija en 3.
@Configuration
public class KafkaTopicsConfig {

    private final int factorReplicacion;

    public KafkaTopicsConfig(
            @Value("${kafka.topics.replication-factor}") int factorReplicacion) {

        this.factorReplicacion = factorReplicacion;
    }

    @Bean
    public NewTopic pedidosEventos() {

        return TopicBuilder.name(KafkaTopicNames.PEDIDOS_EVENTOS)
                // 3 particiones: permite paralelizar el consumo entre varios
                // grupos (analitica-ventas, catalogo-populares) sin saturar un solo broker.
                .partitions(3)
                .replicas(factorReplicacion)
                // Tópico de eventos de negocio: los mensajes viejos no aportan
                // valor una vez procesados, así que se borran por antigüedad.
                .config("cleanup.policy", "delete")
                // 7 días: suficiente para reprocesar o auditar sin crecer indefinidamente.
                .config("retention.ms", "604800000")
                // Con min.insync.replicas=1 el productor puede seguir escribiendo
                // aunque uno de los dos brokers esté caído (RF=2 en este proyecto).
                .config("min.insync.replicas", "1")
                .build();
    }

    @Bean
    public NewTopic inventarioEventos() {

        return TopicBuilder.name(KafkaTopicNames.INVENTARIO_EVENTOS)
                .partitions(3)
                .replicas(factorReplicacion)
                // La clave del evento es productoId: solo interesa el último
                // STOCK_ACTUALIZADO por producto, no el historial completo.
                .compact()
                // Umbral más agresivo que el valor por defecto (0.5) para que
                // la compactación libere espacio con más frecuencia.
                .config("min.cleanable.dirty.ratio", "0.1")
                // Valor de demostración: fuerza segmentos nuevos cada minuto para
                // que la compactación sea visible en la demo. En producción este
                // valor sería mucho mayor (horas), no 60 segundos.
                .config("segment.ms", "60000")
                .build();
    }

    @Bean
    public NewTopic pedidosEventosDlt() {

        return TopicBuilder.name(KafkaTopicNames.PEDIDOS_EVENTOS_DLT)
                // 3 particiones: igual que el tópico de origen (alineado con
                // la topología real en AWS), aunque el volumen de mensajes
                // fallidos sea bajo.
                .partitions(3)
                .replicas(factorReplicacion)
                .config("cleanup.policy", "delete")
                // 30 días: tiempo de margen para que el equipo investigue los
                // mensajes fallidos antes de que se pierdan.
                .config("retention.ms", "2592000000")
                .build();
    }

    @Bean
    public NewTopic inventarioEventosDlt() {

        return TopicBuilder.name(KafkaTopicNames.INVENTARIO_EVENTOS_DLT)
                .partitions(3)
                .replicas(factorReplicacion)
                .config("cleanup.policy", "delete")
                .config("retention.ms", "2592000000")
                .build();
    }

}
