# 🏦 BTG Pactual - API de Gestión de Fondos (Prueba Técnica)

Esta es la implementación del backend para la gestión de fondos de inversión, desarrollada en **Java 21 con Spring Boot 3**.

El proyecto fue diseñado con una arquitectura robusta, escalable y enfocada en buenas prácticas de Clean Code, principios SOLID y un despliegue **Cloud Native** orientado a AWS.

---

## 🏗️ Arquitectura y Decisiones de Diseño

El sistema fue diseñado bajo el paradigma **Serverless / Cloud Native**, cumpliendo con el requerimiento deseable de utilizar recursos gestionados nativos de AWS para garantizar alta disponibilidad:

* **Patrón de Arquitectura:** Arquitectura en capas (Controller, Service, Repository) utilizando DTOs para aislar el modelo de dominio de la capa de presentación.
* **Base de Datos NoSQL (DynamoDB):** Se implementó el patrón *Single Table Design* optimizando los costos y la velocidad de acceso mediante particiones lógicas (PK y SK).
* **Seguridad (AWS Cognito):** Integración con un Proveedor de Identidad (IdP) configurado como Resource Server con validación de tokens JWT (OAuth2) y perfilamiento por roles (ROLE_CLIENTE).
* **Notificaciones Asíncronas:** Las notificaciones (Email/SMS) se delegan a un hilo secundario mediante `@Async` integrándose con el SDK v2 de **Amazon SES** y **Amazon SNS**. Esto garantiza un "Fast Response" al cliente sin bloquear la transacción principal.
* **Manejo de Transacciones y Estado:** El estado de los fondos de un cliente se calcula basándose en un registro inmutable de eventos de APERTURA y CANCELACION, garantizando la integridad transaccional e impidiendo retiros no autorizados.

---

## 🚀 Despliegue en la Nube (AWS CloudFormation)

La infraestructura como código (IaC) está completamente definida en el archivo `infrastructure/cloudformation.yaml`. Este template provisiona de forma automatizada:
1. Tablas en Amazon DynamoDB (btg-clientes, btg-fondos, btg-transacciones).
2. Un clúster de **Amazon ECS (AWS Fargate)** para ejecutar el contenedor de la API de forma serverless.
3. AWS Cognito (User Pool y Grupos) para la gestión de identidades.
4. Roles de IAM aplicando el principio de menor privilegio.

Para aprovisionar el entorno productivo en AWS, ejecute:

    aws cloudformation create-stack \
      --stack-name btg-fondos-stack \
      --template-body file://infrastructure/cloudformation.yaml \
      --capabilities CAPABILITY_NAMED_IAM

---

## 💻 Guía para Ejecución y Pruebas Locales (Entorno Dev)

Para facilitar la evaluación de esta prueba técnica sin requerir credenciales reales de AWS, la aplicación cuenta con un "Bypass de Seguridad" temporal en los controladores (que asume el ID `cliente-123`) y simula el envío de notificaciones mediante logs en consola.

### Requisitos previos:
* Java 21
* Maven
* Docker
* AWS CLI instalado (para interactuar con DynamoDB Local)

### Paso 1: Levantar la Base de Datos Local
Utilice Docker para iniciar DynamoDB en el puerto 8000:

    docker run -p 8000:8000 amazon/dynamodb-local

### Paso 2: Crear y poblar la Base de Datos (Data Seeding)
**Crear tablas**
Ejecute los siguientes comandos en su terminal para crear las tablas utilizadas por la aplicación:

    aws dynamodb create-table --table-name btg-clientes --attribute-definitions AttributeName=PK,AttributeType=S --key-schema AttributeName=PK,KeyType=HASH --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

    aws dynamodb create-table --table-name btg-fondos --attribute-definitions AttributeName=PK,AttributeType=S --key-schema AttributeName=PK,KeyType=HASH --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

    aws dynamodb create-table --table-name btg-transacciones --attribute-definitions AttributeName=PK,AttributeType=S AttributeName=SK,AttributeType=S --key-schema AttributeName=PK,KeyType=HASH AttributeName=SK,KeyType=RANGE --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

**Poblar las tablas**
Ejecute los siguientes comandos en su terminal para insertar el cliente de prueba (con su saldo inicial de $500.000 COP) y el catálogo de fondos:


    # 1. Crear Cliente de prueba
    aws dynamodb put-item --table-name btg-clientes --item "{\"PK\":{\"S\":\"CLIENT#cliente-123\"},\"clienteId\":{\"S\":\"cliente-123\"},\"email\":{\"S\":\"usuario@test.com\"},\"telefono\":{\"S\":\"+573000000000\"},\"saldo\":{\"N\":\"500000\"},\"notificacionPreferencia\":{\"S\":\"EMAIL\"}}" --endpoint-url http://localhost:8000

    # 2. Crear Catálogo de Fondos
    aws dynamodb put-item --table-name btg-fondos --item "{\"PK\":{\"S\":\"FONDO#1\"},\"fondoId\":{\"S\":\"1\"},\"nombre\":{\"S\":\"FPV_BTG_PACTUAL_RECAUDADORA\"},\"montoMinimo\":{\"N\":\"75000\"},\"categoria\":{\"S\":\"FPV\"}}" --endpoint-url http://localhost:8000
    aws dynamodb put-item --table-name btg-fondos --item "{\"PK\":{\"S\":\"FONDO#2\"},\"fondoId\":{\"S\":\"2\"},\"nombre\":{\"S\":\"FPV_BTG_PACTUAL_ECOPETROL\"},\"montoMinimo\":{\"N\":\"125000\"},\"categoria\":{\"S\":\"FPV\"}}" --endpoint-url http://localhost:8000
    aws dynamodb put-item --table-name btg-fondos --item "{\"PK\":{\"S\":\"FONDO#3\"},\"fondoId\":{\"S\":\"3\"},\"nombre\":{\"S\":\"DEUDAPRIVADA\"},\"montoMinimo\":{\"N\":\"50000\"},\"categoria\":{\"S\":\"FIC\"}}" --endpoint-url http://localhost:8000
    aws dynamodb put-item --table-name btg-fondos --item "{\"PK\":{\"S\":\"FONDO#4\"},\"fondoId\":{\"S\":\"4\"},\"nombre\":{\"S\":\"FDO-ACCIONES\"},\"montoMinimo\":{\"N\":\"250000\"},\"categoria\":{\"S\":\"FIC\"}}" --endpoint-url http://localhost:8000
    aws dynamodb put-item --table-name btg-fondos --item "{\"PK\":{\"S\":\"FONDO#5\"},\"fondoId\":{\"S\":\"5\"},\"nombre\":{\"S\":\"FPV_BTG_PACTUAL_DINAMICA\"},\"montoMinimo\":{\"N\":\"100000\"},\"categoria\":{\"S\":\"FPV\"}}" --endpoint-url http://localhost:8000

### Paso 3: Ejecutar la Aplicación
Desde la raíz del proyecto, compile y ejecute con Maven:

    mvn clean install
    mvn spring-boot:run

---

## 📡 Endpoints Principales (API REST)

La API cuenta con validación de datos (`@Valid`) y manejo centralizado de excepciones que retorna códigos de estado HTTP semánticos (400, 404, 422).

### 1. Suscribirse a un Fondo
* **URL:** `POST /api/v1/fondos/suscribir`
* **Body:**

      {
        "fondoId": "1",
        "notificacionPreferencia": "SMS"
      }

### 2. Cancelar una Suscripción
* **URL:** `DELETE /api/v1/fondos/cancelar/{fondoId}`

### 3. Consultar Historial de Transacciones
* **URL:** `GET /api/v1/transacciones`

---

## 🧪 Pruebas Unitarias
El proyecto cuenta con cobertura de pruebas unitarias implementadas con **JUnit 5 y Mockito**, garantizando la correcta ejecución de las reglas de negocio (validación de saldos, control de excepciones e integridad de cancelaciones).

Para ejecutar los tests:

    mvn test

---

## 🗄️ Solución Parte 2 (SQL Relacional)
*Enunciado: Obtener los nombres de los clientes que tienen inscrito algún producto disponible solo en las sucursales que visitan.*

Se implementó una solución óptima utilizando **División Relacional** mediante `INNER JOIN` para garantizar la existencia base, y una subconsulta con `NOT EXISTS` para filtrar las restricciones de "Solo".

    SELECT DISTINCT c.nombre, c.apellidos
    FROM cliente c
    INNER JOIN inscripcion i ON i.idCliente = c.id
    INNER JOIN disponibilidad d ON d.idProducto = i.idProducto
    INNER JOIN visitan v ON v.idCliente = c.id 
                        AND v.idSucursal = d.idSucursal
    WHERE NOT EXISTS (
        SELECT 1 
        FROM disponibilidad d2
        WHERE d2.idProducto = i.idProducto
        AND d2.idSucursal NOT IN (
            SELECT v2.idSucursal 
            FROM visitan v2 
            WHERE v2.idCliente = c.id
        )
    );

---
*Nota: La solución a la Parte 2 (Consulta SQL) se encuentra adjunta en el archivo `Parte2_SQL.sql` en la raíz de este repositorio. Las capturas de pantalla que evidencian el despliegue exitoso de la infraestructura en la nube se encuentran dentro de la carpeta `evidencias_aws/`.*