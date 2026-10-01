# RabbitMQ - Megatech Comics

Infraestructura RabbitMQ para el proyecto Megatech-Comics.

## Arquitectura

La infraestructura está compuesta por dos nodos RabbitMQ:

rabbitmq-1
rabbitmq-2

Ambos nodos forman un cluster RabbitMQ mediante Docker Compose.

## Tecnologías

- Docker
- Docker Compose
- RabbitMQ 4.2 Management

## Credenciales

Usuario:

megatech

Contraseña:

megatech123

## Puertos

### Nodo 1

AMQP:

5672

Dashboard:

http://localhost:15672

### Nodo 2

AMQP:

5673

Dashboard:

http://localhost:15673

## Levantar infraestructura

Desde la carpeta:

infra/rabbitmq

Ejecutar:

docker compose up -d

## Verificar contenedores

docker compose ps

## Verificar cluster

docker exec rabbitmq-1 rabbitmqctl cluster_status

docker exec rabbitmq-2 rabbitmqctl cluster_status

## Detener infraestructura

docker compose down

## Arquitectura

rabbitmq-1
     |
     +---- RabbitMQ Cluster
     |
rabbitmq-2