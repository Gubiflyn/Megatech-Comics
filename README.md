# Megatech-Comics

Sistema de e-commerce para una tienda de cómics, desarrollado como proyecto de evaluación de la asignatura **DSY1107 – Desarrollo Cloud Native I**. El proyecto implementa la arquitectura base exigida por la pauta ("Pedidos360"): autenticación federada con Azure AD, un backend de microservicios en Spring Boot orquestado por un BFF, y despliegue en infraestructura cloud (AWS).

## Qué hace el sistema

- Permite a cualquier visitante **navegar el catálogo de cómics sin necesidad de iniciar sesión** (listado, búsqueda, filtro por género/tipo, detalle de cada título).
- Permite a usuarios de **staff** (personal de la tienda) autenticarse con su cuenta corporativa de Microsoft Entra ID y acceder a su perfil.
- Gestiona **carrito de compra, creación de pedidos y procesamiento de pagos (simulado)**, todo protegido detrás de autenticación.
- Gestiona el catálogo de productos (cómics), sus editoriales/autores asociados, y el inventario/stock disponible por producto.
- Centraliza todo el tráfico del frontend hacia el backend a través de un **Backend For Frontend (BFF)**, que valida la identidad del usuario antes de reenviar cualquier solicitud a los microservicios internos.

## Cómo responde a los requisitos de la evaluación

| Requisito de la pauta | Cómo se implementó |
|---|---|
| Frontend con autenticación federada | React + MSAL (`@azure/msal-react`), autenticado contra un tenant de Microsoft Entra ID (Azure AD). *(La pauta sugería Angular; se usó React con aprobación del docente, dado el conocimiento previo del equipo con este framework.)* |
| Backend en microservicios con Spring Boot | 8 microservicios independientes, cada uno con su propio modelo de datos y base de datos dedicada. |
| BFF que valida el JWT recibido del IDaaS | `bff-service` valida issuer, firma (contra el JWKS de Azure AD), expiración y audience en cada request antes de reenviarla. |
| Backend desplegado en instancias cloud, protegido por API Gateway | Los 8 microservicios corren en una instancia EC2 (AWS), gestionados como servicios `systemd`. Un **API Gateway HTTP** de AWS expone el sistema al exterior, con un **JWT Authorizer nativo** que valida los tokens de Azure AD antes de que la petición llegue siquiera al BFF. |
| Integración con base de datos cloud | MySQL en **Amazon RDS**, con un esquema/base de datos separado por microservicio (aislamiento de datos entre dominios). |
| `.gitignore` configurado | Configurado a nivel de `backend/` y `frontend/`, excluyendo artefactos de build, credenciales (`.env`) y estado de Terraform. |

## Arquitectura

```
React (MSAL)
    │  JWT (Azure AD)
    ▼
API Gateway (AWS)  ──► JWT Authorizer (valida token de Azure AD)
    │
    ▼
BFF (bff-service, :8080)  ──► valida JWT nuevamente, orquesta y enruta
    │
    ├── usuarios-service   (:8081)
    ├── catalogo-service   (:8082)  ← único endpoint público, sin auth
    ├── editoriales-service(:8083)
    ├── inventario-service (:8084)
    ├── carrito-service    (:8085)
    ├── pedidos-service    (:8086)
    └── pagos-service      (:8087)
```

Cada microservicio de dominio expone sus endpoints bajo `/api/<dominio>` y mantiene su propia base de datos MySQL; no hay acceso cruzado directo a las tablas de otro servicio.

### Validación de JWT en profundidad

Además del BFF, los microservicios que gestionan operaciones sensibles del cliente (`inventario-service`, `carrito-service`, `pedidos-service`, `pagos-service`) **validan el JWT de forma independiente**, como capa adicional de seguridad (defensa en profundidad) ante la posibilidad de que alguien intente saltarse el BFF y llamar directamente a un microservicio interno. `usuarios-service`, `catalogo-service` y `editoriales-service` confían en el BFF como único punto de validación, dado que no exponen operaciones críticas del negocio.

## Tecnologías

**Frontend:** React, Vite, React Router, Axios, `@azure/msal-react` / `@azure/msal-browser`

**Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security (OAuth2 Resource Server), Hibernate, MySQL Connector/J

**Base de datos:** MySQL 8.4 (Amazon RDS)

**Identidad:** Microsoft Entra ID (Azure AD) — autenticación OAuth2/OIDC vía MSAL

**Infraestructura / despliegue:** AWS (EC2, API Gateway HTTP, RDS, Elastic IP), gestionada como **infraestructura como código con Terraform** (`backend/infra/`)

## Estructura del repositorio

```
Megatech-Comics/
├── backend/
│   ├── bff-service/           # Orquestador y punto único de validación JWT
│   ├── usuarios-service/      # Staff (Azure AD) — perfil y direcciones
│   ├── catalogo-service/      # Catálogo de cómics (público)
│   ├── editoriales-service/   # Editoriales y autores
│   ├── inventario-service/    # Stock por producto
│   ├── carrito-service/       # Carrito de compra
│   ├── pedidos-service/       # Pedidos y su ciclo de estados
│   ├── pagos-service/         # Procesamiento de pagos (simulado)
│   └── infra/                 # Proyecto Terraform (EC2, API Gateway, despliegue)
└── frontend/                  # Aplicación React
```

## Ejecutar el proyecto localmente

1. Cada microservicio de backend requiere sus propias variables de entorno (conexión a MySQL, y en los 4 servicios que corresponda, `AZURE_ISSUER_URI`/`AZURE_AUDIENCE` para validar JWT). Estas variables se definen en un archivo `.env` por servicio (no versionado en git).
2. Levantar cada microservicio: `mvn spring-boot:run` desde su carpeta respectiva.
3. Levantar el frontend: `npm install && npm run dev` desde `frontend/`.
4. La aplicación queda disponible en `http://localhost:5173`, apuntando por defecto al `bff-service` local en `http://localhost:8080`.

## Comandos para la presentación

### 1. Levantar el frontend

Ejecutar en PowerShell desde el PC:

```powershell
cd C:\proyectos\Megatech-Comics\frontend
npm run dev
```

---

### 2. Verificar servicios activos en EC2

```bash
for s in megatech-bff megatech-usuarios megatech-catalogo megatech-editoriales megatech-inventario megatech-carrito megatech-pedidos megatech-pagos; do
  echo -n "$s: "
  systemctl is-active "$s"
done
```

Todos deberían aparecer como `active`.

---

### 3. Ver el puerto configurado de cada servicio

```bash
for s in megatech-bff megatech-usuarios megatech-catalogo megatech-editoriales megatech-inventario megatech-carrito megatech-pedidos megatech-pagos; do
  echo "===== $s ====="
  systemctl show "$s" -p ExecStart --value
done
```

Puertos:

- BFF: 8080
- Usuarios: 8081
- Catálogo: 8082
- Editoriales: 8083
- Inventario: 8084
- Carrito: 8085
- Pedidos: 8086
- Pagos: 8087

---

### 4. Comprobar puertos activos

```bash
sudo ss -lntp | grep -E ':8080|:8081|:8082|:8083|:8084|:8085|:8086|:8087'
```

---

### 5. Comprobar estado del BFF

```bash
curl -i http://localhost:8080/actuator/health
```

Resultado esperado:

```json
{"status":"UP"}
```

---

### 6. Comprobar microservicio Catálogo

Ver todos los cómics de forma ordenada:

```bash
curl -s http://localhost:8082/api/comics | python3 -m json.tool
```

Ver un cómic específico:

```bash
curl -s http://localhost:8082/api/comics/1 | python3 -m json.tool
```

---

### 7. Comprobar microservicio Editoriales

```bash
curl -s http://localhost:8083/api/editoriales | python3 -m json.tool
```

---

### 8. Conectarse a RDS / MySQL

```bash
DB_HOST=$(sudo sed -n 's/^RDS_HOST=//p' /etc/megatech/megatech.env)
DB_USER=$(sudo sed -n 's/^CATALOGO_DB_USERNAME=//p' /etc/megatech/megatech.env)
DB_PASS=$(sudo sed -n 's/^CATALOGO_DB_PASSWORD=//p' /etc/megatech/megatech.env)

MYSQL_PWD="$DB_PASS" mysql -h "$DB_HOST" -P 3306 -u "$DB_USER"
```

---

### 9. Mostrar bases de datos

Ejecutar una vez dentro de MariaDB/MySQL:

```sql
SHOW DATABASES;
```

---

### 10. Ver cómics junto con su stock

```sql
SELECT
    c.id AS ID,
    c.titulo AS Comic,
    c.precio AS Precio,
    i.stock AS Stock,
    i.stock_minimo AS Stock_Minimo
FROM megatech_catalogo.comics c
LEFT JOIN megatech_inventario.inventarios i
    ON i.producto_id = c.id
ORDER BY c.id;
```

Esta consulta se puede ejecutar antes y después de una compra para demostrar que el stock disminuye.

Ejemplo:

```text
Antes de comprar:   Stock = 10
Después de comprar: Stock = 9
```

---

### 11. Ver editoriales almacenadas

```sql
SELECT *
FROM megatech_editoriales.editoriales;
```

---

### 12. Salir de MySQL

```sql
exit;
```
