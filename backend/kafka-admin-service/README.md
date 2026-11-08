# kafka-admin-service

Microservicio de administración dinámica de Apache Kafka para el proyecto **Megatech-Comics**.

Esta implementación corresponde al trabajo del **Integrante 4** y es el equivalente a `rabbitmq-admin-service`, pero para Kafka: expone una API REST propia sobre `org.apache.kafka.clients.admin.AdminClient` para crear, describir, modificar y eliminar tópicos y consumer groups. **No produce ni consume mensajes de negocio**, solo administra la topología del clúster.

El clúster Kafka lo levanta/administra otro integrante del equipo por separado; este servicio solo se conecta a él.

- **Local** (desarrollo): `infra/kafka`, 2 brokers + Zookeeper vía Docker Compose.
- **AWS** (ambiente desplegado): 3 brokers Kafka ya operativos, con los 4 tópicos del proyecto ya creados (3 particiones, 3 réplicas cada uno).

---

## 1. Puerto y variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `server.port` | `8089` | Puerto HTTP del servicio |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092,localhost:9093` | Lista de brokers Kafka. **No trae IPs de AWS por defecto**, para que el servicio arranque localmente sin depender de esa infraestructura. |
| `KAFKA_TOPIC_REPLICATION_FACTOR` | `2` | Factor de replicación de los tópicos provisionados al arrancar |
| `KAFKA_ADMIN_REQUEST_TIMEOUT_MS` | `5000` | Timeout de request del `AdminClient` propio (API REST) |
| `KAFKA_ADMIN_DEFAULT_API_TIMEOUT_MS` | `10000` | Timeout por defecto de las operaciones del `AdminClient` propio (API REST) |
| `AZURE_ISSUER_URI` | *(obligatoria, sin default)* | Issuer de Microsoft Entra ID, para validar JWT en las operaciones protegidas |
| `AZURE_AUDIENCE` | *(obligatoria, sin default)* | Audience esperada del JWT |

Si Kafka no está disponible al arrancar, el servicio **igual levanta** (`spring.kafka.admin.fail-fast=false` + `spring.kafka.admin.operation-timeout=10s`, para no bloquear el arranque con los timeouts largos por defecto de Kafka) y deja un log claro; las llamadas a la API responderán `503` mientras el clúster no esté accesible.

### Valores para el ambiente AWS

El servicio **no trae estos valores por defecto**; se fijan como variables de entorno al desplegar (por ejemplo, en la unidad `systemd` del servicio en la EC2 de backend):

```text
KAFKA_BOOTSTRAP_SERVERS=172.31.43.118:9092,172.31.43.118:9093,172.31.43.118:9094
KAFKA_TOPIC_REPLICATION_FACTOR=3
```

Como los 4 tópicos ya existen en ese clúster con esa topología, el servicio solo los detecta al arrancar: no los recrea ni les cambia particiones/réplicas.

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
| `pedidos.eventos.DLT` | `pedidos.eventos` | 3 | 30 días |
| `inventario.eventos.DLT` | `inventario.eventos` | 3 | 30 días |

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
| `PUT` | `/topics/{nombre}/config` | 🔒 Modifica configuraciones permitidas (`incrementalAlterConfigs`) | `200` |
| `PUT` | `/topics/{nombre}/partitions` | Aumenta particiones | `200` |
| `DELETE` | `/topics/{nombre}` | 🔒 Elimina el tópico (rechaza internos y los 4 tópicos de negocio) | `204` |

Configuraciones permitidas en `PUT /topics/{nombre}/config`: `retention.ms`, `retention.bytes`, `cleanup.policy`, `segment.ms`, `min.insync.replicas`, `max.message.bytes`.

### Consumer groups

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| `GET` | `/consumer-groups` | Lista id y estado de cada grupo | `200` |
| `GET` | `/consumer-groups/{id}` | Describe estado, coordinador, miembros y LAG por tópico-partición | `200` |
| `DELETE` | `/consumer-groups/{id}` | 🔒 Elimina el grupo (`409` si tiene miembros activos) | `204` |

🔒 = requiere JWT válido (Microsoft Entra ID). Ver sección 3.1.

### Clúster

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/cluster` | Id del clúster, controlador y lista de brokers |

### 3.1 Seguridad

Para evitar romper por accidente el clúster que ya usa el sistema en producción, las operaciones destructivas/sensibles requieren un JWT válido de Microsoft Entra ID (igual que `inventario-service`, `carrito-service`, etc. — ver raíz del repo):

- `DELETE /topics/{nombre}`
- `DELETE /consumer-groups/{id}`
- `PUT /topics/{nombre}/config`

El resto de la API (lecturas, crear tópico, aumentar particiones) queda abierta, igual que hoy la consume el equipo.

Además, `DELETE /topics/{nombre}` rechaza los 4 tópicos de negocio (`pedidos.eventos`, `inventario.eventos` y sus `.DLT`) **aunque la petición esté autenticada**: no se pueden borrar por esta API bajo ninguna circunstancia.

### Errores

| Código | Causa |
|---|---|
| `400` | Validación de body (`@Valid`), configuración inválida (clave no permitida, `cleanup.policy` inválido, `retention.ms` no numérico, aumentar particiones a un valor menor o igual al actual), o intento de eliminar un tópico interno/de negocio protegido |
| `401` / `403` | Falta o es inválido el JWT en una operación protegida |
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

# Cambiar retention.ms (protegido: requiere JWT)
curl -X PUT http://localhost:8089/api/kafka/topics/demo.topico/config \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"configuraciones":{"retention.ms":"86400000"}}'

# Aumentar particiones
curl -X PUT http://localhost:8089/api/kafka/topics/demo.topico/partitions \
  -H "Content-Type: application/json" \
  -d '{"nuevoNumeroParticiones":6}'

# Eliminar tópico (protegido: requiere JWT; rechaza los 4 tópicos de negocio)
curl -X DELETE http://localhost:8089/api/kafka/topics/demo.topico \
  -H "Authorization: Bearer $TOKEN"

# Listar consumer groups
curl http://localhost:8089/api/kafka/consumer-groups

# Describir consumer group
curl http://localhost:8089/api/kafka/consumer-groups/analitica-ventas

# Eliminar consumer group (protegido: requiere JWT)
curl -X DELETE http://localhost:8089/api/kafka/consumer-groups/analitica-ventas \
  -H "Authorization: Bearer $TOKEN"

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

`AZURE_ISSUER_URI` y `AZURE_AUDIENCE` son obligatorias (sin ellas el servicio no arranca): usar el mismo tenant/app registration de Entra ID que el resto de los microservicios.

```bash
cd backend/kafka-admin-service
AZURE_ISSUER_URI=... AZURE_AUDIENCE=... mvn spring-boot:run
```
