# kafka-admin-service

Microservicio de administración dinámica de Apache Kafka para el proyecto **Megatech-Comics**.

Esta implementación corresponde al trabajo del **Integrante 4** y es el equivalente a `rabbitmq-admin-service`, pero para Kafka: expone una API REST propia sobre `org.apache.kafka.clients.admin.AdminClient` para crear, describir, modificar y eliminar tópicos y consumer groups. **No produce ni consume mensajes de negocio**, solo administra la topología del clúster.

El clúster Kafka (2 brokers + Zookeeper, vía Docker Compose en `infra/kafka`) lo levanta otro integrante del equipo por separado; este servicio solo se conecta a él.

---

## 1. Puerto y variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `server.port` | `8089` | Puerto HTTP del servicio |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092,localhost:9093` | Lista de brokers Kafka |
| `KAFKA_TOPIC_REPLICATION_FACTOR` | `2` | Factor de replicación de los tópicos provisionados al arrancar |
| `KAFKA_ADMIN_REQUEST_TIMEOUT_MS` | `5000` | Timeout de request del `AdminClient` |
| `KAFKA_ADMIN_DEFAULT_API_TIMEOUT_MS` | `10000` | Timeout por defecto de las operaciones del `AdminClient` |

Si Kafka no está disponible al arrancar, el servicio **igual levanta** (`spring.kafka.admin.fail-fast=false`) y deja un log claro; las llamadas a la API responderán `503` mientras el clúster no esté accesible.

---

## 2. Contrato del equipo

### Tópicos de negocio

| Tópico | Eventos | Particiones | Cleanup policy |
|---|---|---|---|
| `pedidos.eventos` | `PEDIDO_CREADO`, `PEDIDO_PAGADO` | 3 | `delete` (retention 7 días) |
| `inventario.eventos` | `STOCK_ACTUALIZADO` | 3 | `compact` (clave = `productoId`) |

### Dead Letter Topics

Convención por defecto de Spring Kafka: `<tópico>.DLT`.

| Tópico | Origen | Particiones | Retención |
|---|---|---|---|
| `pedidos.eventos.DLT` | `pedidos.eventos` | 1 | 30 días |
| `inventario.eventos.DLT` | `inventario.eventos` | 1 | 30 días |

### Consumer groups (documentados; los usan otros servicios)

| Grupo | Tópico que consume |
|---|---|
| `analitica-ventas` | `pedidos.eventos` |
| `analitica-stock` | `inventario.eventos` |
| `catalogo-populares` | `pedidos.eventos` |

Todos los nombres anteriores están centralizados en `config/KafkaTopicNames.java`. Ningún otro archivo del servicio debe escribirlos a mano.

---

## 3. Endpoints

Base: `/api/kafka`

### Tópicos

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| `POST` | `/topics` | Crea un tópico | `201` |
| `GET` | `/topics` | Lista nombres de tópicos (excluye internos `__*`) | `200` |
| `GET` | `/topics/{nombre}` | Describe particiones (líder, réplicas, ISR) y configuraciones | `200` |
| `PUT` | `/topics/{nombre}/config` | Modifica configuraciones permitidas (`incrementalAlterConfigs`) | `200` |
| `PUT` | `/topics/{nombre}/partitions` | Aumenta particiones | `200` |
| `DELETE` | `/topics/{nombre}` | Elimina el tópico (rechaza internos) | `204` |

Configuraciones permitidas en `PUT /topics/{nombre}/config`: `retention.ms`, `retention.bytes`, `cleanup.policy`, `segment.ms`, `min.insync.replicas`, `max.message.bytes`.

### Consumer groups

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| `GET` | `/consumer-groups` | Lista id y estado de cada grupo | `200` |
| `GET` | `/consumer-groups/{id}` | Describe estado, coordinador, miembros y LAG por tópico-partición | `200` |
| `DELETE` | `/consumer-groups/{id}` | Elimina el grupo (`409` si tiene miembros activos) | `204` |

### Clúster

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/cluster` | Id del clúster, controlador y lista de brokers |

### Errores

| Código | Causa |
|---|---|
| `400` | Validación de body (`@Valid`) o configuración inválida (clave no permitida, `cleanup.policy` inválido, `retention.ms` no numérico, aumentar particiones a un valor menor o igual al actual) |
| `404` | Tópico o consumer group inexistente |
| `409` | Tópico ya existe, o consumer group con miembros activos |
| `503` | Kafka no disponible (timeout del `AdminClient`) |

---

## 4. Ejemplos curl

```bash
# Crear tópico
curl -X POST http://localhost:8089/api/kafka/topics \
  -H "Content-Type: application/json" \
  -d '{"nombre":"demo.topico","particiones":3,"factorReplicacion":2}'

# Listar tópicos
curl http://localhost:8089/api/kafka/topics

# Describir tópico
curl http://localhost:8089/api/kafka/topics/demo.topico

# Cambiar retention.ms
curl -X PUT http://localhost:8089/api/kafka/topics/demo.topico/config \
  -H "Content-Type: application/json" \
  -d '{"configuraciones":{"retention.ms":"86400000"}}'

# Aumentar particiones
curl -X PUT http://localhost:8089/api/kafka/topics/demo.topico/partitions \
  -H "Content-Type: application/json" \
  -d '{"nuevoNumeroParticiones":6}'

# Eliminar tópico
curl -X DELETE http://localhost:8089/api/kafka/topics/demo.topico

# Listar consumer groups
curl http://localhost:8089/api/kafka/consumer-groups

# Describir consumer group
curl http://localhost:8089/api/kafka/consumer-groups/analitica-ventas

# Eliminar consumer group
curl -X DELETE http://localhost:8089/api/kafka/consumer-groups/analitica-ventas

# Info del clúster
curl http://localhost:8089/api/kafka/cluster
```

---

## 5. Desarrollo local

Requiere el clúster de `infra/kafka` levantado (ver su propio README/`.env.example`):

```bash
cd infra/kafka
docker compose up -d
```

Con un único broker local, usar `KAFKA_TOPIC_REPLICATION_FACTOR=1` (el default de este servicio es `2`, pensado para los 2 brokers del compose del proyecto).

```bash
cd backend/kafka-admin-service
mvn spring-boot:run
```
