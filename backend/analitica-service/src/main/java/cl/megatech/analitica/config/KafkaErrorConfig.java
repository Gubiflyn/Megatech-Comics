package cl.megatech.analitica.config;

import org.apache.kafka.common.TopicPartition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(KafkaErrorConfig.class);

    @Bean
    public CommonErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, String> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) -> {

                            String destino = record.topic() + ".DLT";

                            LOGGER.error(
                                    "[KAFKA DLT] Enviando mensaje fallido. " +
                                    "topic={} | particion={} | offset={} | destino={}",
                                    record.topic(),
                                    record.partition(),
                                    record.offset(),
                                    destino,
                                    exception
                            );

                            // Los topics DLT tienen una sola particion.
                            return new TopicPartition(destino, 0);
                        }
                );

        // Si falla el envio a DLT, no se considera recuperado.
        recoverer.setFailIfSendResultIsError(true);

        // Dos reintentos con un segundo entre ellos.
        FixedBackOff backOff = new FixedBackOff(
                1000L,
                2L
        );

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, backOff);

        // Datos invalidos: enviar directamente a DLT.
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class
        );

        return errorHandler;
    }
}
