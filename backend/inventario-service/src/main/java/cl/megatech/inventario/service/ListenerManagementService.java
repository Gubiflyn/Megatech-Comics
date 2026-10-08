package cl.megatech.inventario.service;

import org.springframework.amqp.core.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ListenerManagementService {

    private final RabbitListenerEndpointRegistry registry;

    public ListenerManagementService(
            RabbitListenerEndpointRegistry registry) {

        this.registry = registry;
    }

    public List<Map<String, Object>> listarListeners() {

        return registry
                .getListenerContainerIds()
                .stream()
                .sorted()
                .map(id -> {

                    MessageListenerContainer container =
                            registry.getListenerContainer(id);

                    Map<String, Object> estado =
                            new LinkedHashMap<>();

                    estado.put("id", id);
                    estado.put(
                            "running",
                            container != null
                                    && container.isRunning()
                    );

                    return estado;
                })
                .toList();
    }

    public boolean pausarListener(String listenerId) {

        MessageListenerContainer container =
                registry.getListenerContainer(listenerId);

        if (container == null) {
            return false;
        }

        if (container.isRunning()) {
            container.stop();
        }

        return true;
    }

    public boolean reanudarListener(String listenerId) {

        MessageListenerContainer container =
                registry.getListenerContainer(listenerId);

        if (container == null) {
            return false;
        }

        if (!container.isRunning()) {
            container.start();
        }

        return true;
    }
}