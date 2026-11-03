# Megatech-Comics — Infraestructura Kafka (EP3)

Plantilla propuesta para la rama `feature/kafka-infra`. No modifica la infraestructura RabbitMQ existente.

## Contenido

- `infra/kafka/docker-compose.yml`: ZooKeeper y 2 brokers Kafka (Confluent 7.9.10, ZooKeeper mode).
- `infra/kafka/.env.example`: IP o nombre que Kafka anuncia a clientes fuera de Docker.
- `infra/kafka-ui/docker-compose.yml`: Kafka UI (localhost:8088 mediante túnel SSH en AWS).
- `infra/nginx/megatech.conf.example`: servidor estático básico para Vite/React.

## 1. Preparación local

Desde la raíz del repositorio:

```bash
cp infra/kafka/.env.example infra/kafka/.env
# Para el equipo local, mantener KAFKA_ADVERTISED_HOST=localhost.
docker compose -f infra/kafka/docker-compose.yml config
docker compose -f infra/kafka/docker-compose.yml up -d
docker compose -f infra/kafka/docker-compose.yml ps
```

La red `megatech-kafka-net` se crea en el primer `up`. Levantar la interfaz:

```bash
docker compose -f infra/kafka-ui/docker-compose.yml config
docker compose -f infra/kafka-ui/docker-compose.yml up -d
docker compose -f infra/kafka-ui/docker-compose.yml ps
```

Visitar http://localhost:8088 (solo accesible desde el host donde corre Docker).

## 2. Validación del clúster

```bash
docker exec megatech-kafka-1 kafka-topics --bootstrap-server kafka-1:29092 --list
docker exec megatech-kafka-1 kafka-broker-api-versions --bootstrap-server kafka-1:29092
```

Comprobar en la segunda salida que figuren ambos IDs de broker. La primera salida puede estar vacía si aún no se crearon los dos tópicos de negocio.

Prueba opcional creando un tópico temporal, con 2 particiones y 2 réplicas:

```bash
docker exec megatech-kafka-1 kafka-topics --bootstrap-server kafka-1:29092 --create --if-not-exists --topic ep3.prueba --partitions 2 --replication-factor 2
docker exec megatech-kafka-1 kafka-topics --bootstrap-server kafka-1:29092 --describe --topic ep3.prueba
```

Enviar un mensaje de prueba (Ctrl+C para cerrar):

```bash
docker exec -it megatech-kafka-1 kafka-console-producer --bootstrap-server kafka-1:29092 --topic ep3.prueba
```

En otra terminal, leer desde el inicio (Ctrl+C para cerrar):

```bash
docker exec -it megatech-kafka-2 kafka-console-consumer --bootstrap-server kafka-2:29093 --topic ep3.prueba --from-beginning
```

Al terminar, borrar SOLO el tópico temporal si no se necesita:

```bash
docker exec megatech-kafka-1 kafka-topics --bootstrap-server kafka-1:29092 --delete --topic ep3.prueba
```

## 3. Para AWS: advertised.listeners

La propiedad `KAFKA_ADVERTISED_HOST` **debe ser la IP privada estable de la EC2 Kafka** si los microservicios están en otra EC2 de la misma VPC. Actualizar `infra/kafka/.env` en el servidor:

```dotenv
KAFKA_ADVERTISED_HOST=IP_PRIVADA_EC2_KAFKA
```

En los microservicios Spring Boot, usar:

```properties
spring.kafka.bootstrap-servers=IP_PRIVADA_EC2_KAFKA:9092,IP_PRIVADA_EC2_KAFKA:9093
```

`localhost` solo funciona cuando el cliente corre en el mismo host que Docker. En Docker, los servicios Kafka UI y otros contenedores de la misma red usan `kafka-1:29092,kafka-2:29093`.

Aplicar la nueva configuración recreando SOLO Kafka, no RabbitMQ:

```bash
docker compose -f infra/kafka/docker-compose.yml up -d --force-recreate
```

**No usar** `docker compose down -v` porque eliminaría volúmenes y mensajes.

## 4. Security Groups recomendados

Para la EC2 Kafka:

- TCP 22: únicamente IP pública de administración (SSH).
- TCP 9092 y 9093: **origen = Security Group del backend**, dentro de la misma VPC.
- TCP 2181, 29092 y 29093: no publicar hacia Internet (no requieren reglas SG).
- TCP 8088: no abrir hacia Internet; Kafka UI está limitada a `127.0.0.1`.

Desde el equipo personal, acceder a Kafka UI mediante túnel SSH, con una sesión SSH abierta:

```bash
ssh -i RUTA_CLAVE.pem -L 8088:127.0.0.1:8088 ec2-user@IP_PUBLICA_EC2_KAFKA
```

Abrir `http://localhost:8088` en el computador local.

**Recursos**: dos brokers Kafka, ZooKeeper y Kafka UI consumen bastante memoria. No instalarlos en la actual EC2 RabbitMQ `t2.small` sin dimensionar memoria, RAM libre y disco. Se recomienda una EC2 Kafka separada con al menos 4 GiB RAM para esta demostración con límites de heap reducidos; 8 GiB da más margen. Comprobar costos y límites AWS Academy antes de crearla.

## 5. Frontend con Nginx

El frontend existente es Vite + React. En la instancia que publique el frontend:

```bash
cd frontend
npm ci
npm run build
```

Copiar el contenido de `frontend/dist/` a `/usr/share/nginx/html/` y usar la configuración `infra/nginx/megatech.conf.example` para soportar rutas SPA con `try_files`. Primero comprobar HTML y recursos estáticos.

Para **MSAL + Microsoft Entra ID en una URL pública**, se necesita un dominio/URL HTTPS admitido y registrar exactamente su Redirect URI como SPA en Entra ID. `http://localhost:5173` solo corresponde al entorno local; no sustituirlo por una IP pública HTTP sin verificar restricciones de Microsoft.

## 6. Evidencias para EP3

Capturar: `docker compose ps`, salida de `kafka-broker-api-versions`, `kafka-topics --describe` en dos tópicos reales, mensajes en Kafka UI, conectividad desde la EC2 backend a puertos 9092 y 9093, SG restringidos, página servida con Nginx, login y consumo autenticado una vez integrados.

## 7. Limitaciones

- Kafka en ZooKeeper mode se utiliza para cumplir la consigna EP3; en Kafka moderno KRaft es la opción recomendada.
- 2 brokers = factor de réplica máximo 2. Si se exige RF=3, hace falta al menos 3 brokers.
- `PLAINTEXT` es apropiado solo para un laboratorio protegido por la red privada y Security Groups; producción necesita autenticación y cifrado.
- No se han ejecutado contenedores Docker en la generación de este archivo: validar con `docker compose config` y después `up` en el equipo o EC2.
