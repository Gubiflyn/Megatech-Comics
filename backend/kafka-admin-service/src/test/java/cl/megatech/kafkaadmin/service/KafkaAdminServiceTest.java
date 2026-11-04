package cl.megatech.kafkaadmin.service;

import cl.megatech.kafkaadmin.dto.CrearTopicoRequest;
import cl.megatech.kafkaadmin.exception.KafkaNoDisponibleException;
import cl.megatech.kafkaadmin.exception.RecursoYaExisteException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.internals.KafkaFutureImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaAdminServiceTest {

    @Mock
    private AdminClient adminClient;

    @Mock
    private CreateTopicsResult createTopicsResult;

    @Test
    void crearTopico_traduceTopicExistsException_aRecursoYaExiste() {

        KafkaFutureImpl<Void> futuroFallido = new KafkaFutureImpl<>();
        futuroFallido.completeExceptionally(new TopicExistsException("ya existe"));

        when(adminClient.createTopics(anyCollection())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(futuroFallido);

        KafkaAdminService service = new KafkaAdminService(adminClient);

        CrearTopicoRequest request = new CrearTopicoRequest(
                "pedidos.eventos", 3, 2, null
        );

        assertThatThrownBy(() -> service.crearTopico(request))
                .isInstanceOf(RecursoYaExisteException.class);
    }

    @Test
    void crearTopico_traduceTimeoutException_aKafkaNoDisponible() {

        KafkaFutureImpl<Void> futuroFallido = new KafkaFutureImpl<>();
        futuroFallido.completeExceptionally(new TimeoutException("sin respuesta"));

        when(adminClient.createTopics(anyCollection())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(futuroFallido);

        KafkaAdminService service = new KafkaAdminService(adminClient);

        CrearTopicoRequest request = new CrearTopicoRequest(
                "inventario.eventos", 3, 2, null
        );

        assertThatThrownBy(() -> service.crearTopico(request))
                .isInstanceOf(KafkaNoDisponibleException.class);
    }

    @Test
    void crearTopico_exitoso_noLanzaExcepcion() {

        when(adminClient.createTopics(anyCollection())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(KafkaFuture.completedFuture(null));

        KafkaAdminService service = new KafkaAdminService(adminClient);

        CrearTopicoRequest request = new CrearTopicoRequest(
                "pedidos.eventos", 3, 2, null
        );

        service.crearTopico(request);
    }

}
