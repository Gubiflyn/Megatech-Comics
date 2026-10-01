# RabbitMQ - Megatech Comics

Infraestructura RabbitMQ para el proyecto **Megatech-Comics**.

Esta infraestructura corresponde al trabajo del **Integrante 1** y está completamente separada de los microservicios del proyecto.

Su objetivo es proporcionar un **cluster RabbitMQ de 2 nodos** mediante Docker Compose, tanto para desarrollo local como para ejecución en AWS EC2.

---

## 1. Arquitectura

La infraestructura está formada por dos nodos RabbitMQ:

```text
             RabbitMQ Cluster
          ┌─────────────────────┐
          │                     │
   rabbitmq-1              rabbitmq-2
      │                        │
   AMQP 5672                AMQP 5673
   UI 15672                 UI 15673
```

Ambos nodos utilizan la misma **Erlang Cookie**, permitiendo que RabbitMQ los reconozca como miembros del mismo cluster.

---

## 2. Estructura del proyecto

```text
Megatech-Comics/
└── infra/
    └── rabbitmq/
        ├── docker-compose.yml
        ├── rabbitmq.conf
        └── README.md
```

Esta infraestructura no modifica directamente:

```text
pedidos-service
pagos-service
inventario-service
catalogo-service
usuarios-service
editoriales-service
carrito-service
bff-service
```

---

## 3. Tecnologías utilizadas

- Docker
- Docker Compose
- RabbitMQ 4.2 Management
- Erlang
- AWS EC2
- Amazon Linux 2023

---

## 4. Nodos RabbitMQ

### rabbitmq-1

```text
Nombre del nodo:
rabbit@rabbitmq-1

Puerto AMQP:
5672

Dashboard:
15672
```

### rabbitmq-2

```text
Nombre del nodo:
rabbit@rabbitmq-2

Puerto AMQP:
5673

Dashboard:
15673
```

---

## 5. Credenciales RabbitMQ

```text
Usuario:
megatech

Contraseña:
megatech123
```

> Estas credenciales corresponden al ambiente académico del proyecto y deben cambiarse si la solución se utiliza en un ambiente real o productivo.

---

# DESARROLLO LOCAL

## 6. Levantar RabbitMQ localmente

Desde la raíz del proyecto:

```bash
cd infra/rabbitmq
```

Validar primero la configuración:

```bash
docker compose config
```

Levantar los dos nodos:

```bash
docker compose up -d
```

---

## 7. Verificar contenedores

```bash
docker compose ps
```

El resultado esperado es:

```text
rabbitmq-1   Up (...) (healthy)
rabbitmq-2   Up (...) (healthy)
```

---

## 8. Verificar el cluster

Ejecutar:

```bash
docker exec rabbitmq-1 rabbitmqctl cluster_status
```

El resultado debe incluir:

```text
Disk Nodes

rabbit@rabbitmq-1
rabbit@rabbitmq-2
```

y:

```text
Running Nodes

rabbit@rabbitmq-1
rabbit@rabbitmq-2
```

También debería indicar:

```text
Alarms

(none)

Network Partitions

(none)
```

---

## 9. Verificar los nodos individualmente

Nodo 1:

```bash
docker exec rabbitmq-1 rabbitmq-diagnostics ping
```

Nodo 2:

```bash
docker exec rabbitmq-2 rabbitmq-diagnostics ping
```

---

## 10. Dashboards locales

### Nodo 1

```text
http://localhost:15672
```

### Nodo 2

```text
http://localhost:15673
```

Credenciales:

```text
Usuario: megatech
Contraseña: megatech123
```

En la sección **Overview → Nodes** deben aparecer:

```text
rabbit@rabbitmq-1
rabbit@rabbitmq-2
```

---

# DESPLIEGUE EN AWS EC2

## 11. Arquitectura en AWS

RabbitMQ fue desplegado en una EC2 independiente de la EC2 donde funcionan los microservicios.

```text
                    AWS
                     │
         ┌───────────┴───────────┐
         │                       │
   EC2 Backend             EC2 RabbitMQ
         │                       │
   Microservicios          Docker Compose
                                 │
                     ┌───────────┴───────────┐
                     │                       │
                rabbitmq-1              rabbitmq-2
                  :5672                   :5673
                  :15672                  :15673
```

De esta forma, RabbitMQ queda separado de los microservicios.

---

## 12. EC2 RabbitMQ

Sistema operativo:

```text
Amazon Linux 2023
```

Tipo de instancia utilizado:

```text
t2.small
```

IP privada utilizada actualmente:

```text
172.31.35.96
```

IP pública utilizada durante el despliegue:

```text
18.234.24.156
```

> La IP pública puede cambiar si la instancia es detenida e iniciada nuevamente, a menos que se utilice una Elastic IP.

---

## 13. Instalar Docker en EC2

Actualizar paquetes:

```bash
sudo dnf update -y
```

Instalar Docker:

```bash
sudo dnf install -y docker
```

Iniciar y habilitar Docker:

```bash
sudo systemctl enable --now docker
```

Agregar `ec2-user` al grupo Docker:

```bash
sudo usermod -aG docker ec2-user
```

Cerrar la sesión:

```bash
exit
```

Volver a conectarse por SSH.

Comprobar Docker:

```bash
docker --version
```

```bash
docker ps
```

---

## 14. Instalar Docker Compose

En Amazon Linux 2023 se instaló Docker Compose como plugin.

Crear carpeta:

```bash
mkdir -p ~/.docker/cli-plugins
```

Descargar Docker Compose:

```bash
curl -SL https://github.com/docker/compose/releases/download/v5.5.0/docker-compose-linux-x86_64 \
-o ~/.docker/cli-plugins/docker-compose
```

Dar permisos:

```bash
chmod +x ~/.docker/cli-plugins/docker-compose
```

Comprobar:

```bash
docker compose version
```

Versión utilizada:

```text
Docker Compose version v5.5.0
```

---

## 15. Clonar la rama de infraestructura

Desde EC2:

```bash
cd /home/ec2-user
```

Clonar únicamente la rama:

```bash
git clone -b infra-rabbitmq --single-branch https://github.com/Gubiflyn/Megatech-Comics.git
```

Entrar al repositorio:

```bash
cd Megatech-Comics
```

Comprobar rama:

```bash
git branch
```

Resultado esperado:

```text
* infra-rabbitmq
```

---

## 16. Levantar RabbitMQ en EC2

Entrar a:

```bash
cd /home/ec2-user/Megatech-Comics/infra/rabbitmq
```

Validar:

```bash
docker compose config
```

Levantar los nodos:

```bash
docker compose up -d
```

Comprobar:

```bash
docker compose ps
```

Resultado esperado:

```text
rabbitmq-1   Up (...) (healthy)
rabbitmq-2   Up (...) (healthy)
```

---

## 17. Verificar cluster en EC2

```bash
docker exec rabbitmq-1 rabbitmqctl cluster_status
```

Resultado validado:

```text
Disk Nodes

rabbit@rabbitmq-1
rabbit@rabbitmq-2
```

```text
Running Nodes

rabbit@rabbitmq-1
rabbit@rabbitmq-2
```

Además:

```text
Alarms

(none)
```

```text
Network Partitions

(none)
```

---

# SEGURIDAD AWS

## 18. Security Group de RabbitMQ

La EC2 RabbitMQ utiliza reglas de entrada restringidas.

### SSH

```text
Puerto: 22
Origen: IP del administrador
```

### RabbitMQ AMQP

```text
Puerto: 5672
Origen: Security Group de la EC2 backend
```

```text
Puerto: 5673
Origen: Security Group de la EC2 backend
```

### RabbitMQ Management

```text
Puerto: 15672
Origen: IP del administrador
```

```text
Puerto: 15673
Origen: IP del administrador
```

Los puertos AMQP no están abiertos públicamente mediante:

```text
0.0.0.0/0
```

Solo la EC2 backend tiene permitido conectarse.

---

## 19. Security Group del backend

Security Group utilizado para permitir acceso desde los microservicios:

```text
sg-00db636b5bacc9f34
megatech-ec2-sg
```

Este Security Group es utilizado como origen de las reglas:

```text
5672
5673
```

de la EC2 RabbitMQ.

---

# DASHBOARDS AWS

## 20. Dashboard nodo 1

```text
http://18.234.24.156:15672
```

## 21. Dashboard nodo 2

```text
http://18.234.24.156:15673
```

Credenciales:

```text
Usuario: megatech
Contraseña: megatech123
```

En ambos dashboards se verificó la presencia de:

```text
rabbit@rabbitmq-1
rabbit@rabbitmq-2
```

---

# CONECTIVIDAD CON BACKEND

## 22. Conexión desde EC2 Backend

EC2 Backend:

```text
IP privada:
172.31.19.250
```

EC2 RabbitMQ:

```text
IP privada:
172.31.35.96
```

La comunicación se realiza utilizando la red privada de AWS.

---

## 23. Probar nodo 1 desde backend

Desde la EC2 backend:

```bash
timeout 3 bash -c '</dev/tcp/172.31.35.96/5672' && echo "RabbitMQ nodo 1 OK" || echo "RabbitMQ nodo 1 FALLÓ"
```

Resultado obtenido:

```text
RabbitMQ nodo 1 OK
```

---

## 24. Probar nodo 2 desde backend

```bash
timeout 3 bash -c '</dev/tcp/172.31.35.96/5673' && echo "RabbitMQ nodo 2 OK" || echo "RabbitMQ nodo 2 FALLÓ"
```

Resultado obtenido:

```text
RabbitMQ nodo 2 OK
```

Con esto queda comprobada la comunicación:

```text
EC2 Backend
     │
     ├── 172.31.35.96:5672
     │            ↓
     │       rabbitmq-1
     │
     └── 172.31.35.96:5673
                  ↓
             rabbitmq-2
```

---

# CONEXIÓN PARA MICROSERVICIOS

## 25. Parámetros disponibles

Los microservicios desplegados en la EC2 backend pueden conectarse utilizando:

### Nodo principal

```properties
spring.rabbitmq.host=172.31.35.96
spring.rabbitmq.port=5672
spring.rabbitmq.username=megatech
spring.rabbitmq.password=megatech123
```

### Segundo nodo

```text
Host:
172.31.35.96

Puerto:
5673
```

La configuración final de cada microservicio será responsabilidad de los integrantes encargados de los flujos de mensajería.

---

# OPERACIONES

## 26. Ver estado

```bash
docker compose ps
```

---

## 27. Ver logs

Todos los nodos:

```bash
docker compose logs
```

Nodo 1:

```bash
docker logs rabbitmq-1
```

Nodo 2:

```bash
docker logs rabbitmq-2
```

---

## 28. Reiniciar cluster

```bash
docker compose restart
```

---

## 29. Detener cluster

```bash
docker compose stop
```

---

## 30. Volver a iniciar cluster

```bash
docker compose start
```

---

## 31. Detener y eliminar contenedores

```bash
docker compose down
```

Los volúmenes permanecen almacenados.

---

## 32. Eliminar también los volúmenes

Solo utilizar si se desea eliminar completamente los datos de RabbitMQ:

```bash
docker compose down -v
```

---

# RESPONSABILIDADES DEL EQUIPO

## Integrante 1

Infraestructura RabbitMQ:

```text
Docker Compose
2 nodos
Cluster
EC2
Security Groups
Conectividad
```

## Integrante 2

Flujo:

```text
pedido.creado
```

Arquitectura:

```text
Pedidos
   ↓
RabbitMQ
   ↓
Inventario
```

Incluye:

```text
ACK
NACK
DLX
DLQ
```

## Integrante 3

Flujo:

```text
pago.procesado
```

Arquitectura:

```text
Pagos
   ↓
RabbitMQ
   ↓
Pedidos
```

Incluye:

```text
ACK
NACK
DLX
DLQ
```

## Integrante 4

Nuevo microservicio:

```text
rabbitmq-admin-service
```

Responsable de la administración programática de:

```text
Queues
Exchanges
Bindings
Validaciones
API REST
```

---

# ESTADO DE LA INFRAESTRUCTURA

Actualmente se encuentra validado:

```text
[OK] Docker local
[OK] Docker Compose local
[OK] rabbitmq-1 local
[OK] rabbitmq-2 local
[OK] Cluster local
[OK] Dashboard local nodo 1
[OK] Dashboard local nodo 2

[OK] EC2 RabbitMQ
[OK] Amazon Linux 2023
[OK] Docker EC2
[OK] Docker Compose EC2
[OK] rabbitmq-1 EC2
[OK] rabbitmq-2 EC2
[OK] Cluster EC2
[OK] Dashboard EC2 nodo 1
[OK] Dashboard EC2 nodo 2
[OK] Security Groups
[OK] Backend → RabbitMQ 5672
[OK] Backend → RabbitMQ 5673
```

---

# Nota sobre alta disponibilidad

La solución utiliza dos nodos RabbitMQ dentro de una misma instancia EC2.

Esto permite demostrar:

```text
Clustering
Configuración compartida
Comunicación entre nodos
Administración centralizada
```

Sin embargo, ambos contenedores dependen de la misma EC2.

Por lo tanto, esta arquitectura no proporciona tolerancia ante la caída completa de la instancia EC2.

En un ambiente productivo, los nodos RabbitMQ deberían distribuirse entre distintas instancias o zonas de disponibilidad según los requisitos de disponibilidad del sistema.

---

# Resultado

La infraestructura RabbitMQ de Megatech-Comics queda disponible para que los demás microservicios implementen comunicación asíncrona sin necesitar instalar o administrar directamente el broker.

```text
                         Megatech-Comics
                               │
                               ▼
                         EC2 RabbitMQ
                               │
                   ┌───────────┴───────────┐
                   │                       │
              rabbitmq-1              rabbitmq-2
                   │                       │
                   └───────────┬───────────┘
                               │
                       RabbitMQ Cluster
                               │
              ┌────────────────┼────────────────┐
              │                │                │
       pedido.creado    pago.procesado   RabbitAdmin
       Integrante 2     Integrante 3     Integrante 4
```