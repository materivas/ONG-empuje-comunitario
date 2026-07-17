# ONG Empuje Comunitario
Este proyecto implementa un sistema con **gRPC (Java/Spring Boot, NodeJS)**, **API Gateway (NodeJS/ExpressJS)**, **Kafka**, **MySQL**, **GraphQL** y **Mailhog** para pruebas de envío de correos.

---

## 🚀 Tecnologías usadas
- **Java 17 + Spring Boot** → Servidor gRPC
- **Node.js + Express** → API Gateway (REST → gRPC, Kafka, SOAP)
- **MySQL 8** → Base de datos
- **Mailhog** → Captura de correos (testing)
- **Docker + Docker Compose** → Orquestación de servicios
- **Node.js + Express + GraphQL** → Servidor GraphQL
- **Kafka** → Medio de comunicación publicador/suscriptor
- **Java 17 + Spring Boot + Swagger** → Servidor que exporta datos en formato Excel

---

## Documentación

- [Documentación del gRPC Server](https://github.com/UlisesChoco/Sistemas-Distribuidos-Grupo-C-TP/blob/master/grpc%20server/README.md)
- [Documentación del gRPC Client](https://github.com/UlisesChoco/Sistemas-Distribuidos-Grupo-C-TP/blob/master/grpc%20client/README.md)
- [Documentación del GraphQL Server](https://github.com/UlisesChoco/ONG-Empuje-Comunitario/blob/master/graphql-server/Readme.md)

---
## 📋 Prerrequisitos

Antes de comenzar, se debe tener instalado:
- Docker 🐳
- Git 📦

## 1. Clonar el repositorio

```bash
git clone https://github.com/UlisesChoco/ONG-Empuje-Comunitario.git
cd ONG-Empuje-Comunitario
```

## 2. Configurar variables de entorno
Copiar el archivo de ejemplo y completar según sea necesario:
```bash
cp .env.template .env
```
En el archivo .env se debe definir:

- Credenciales de MySQL
- Configuración de la datasource de Spring Boot
- JWT Key (Puede ser cualquier cadena de texto)
- Configuración de Mailhog

Ejemplo mínimo:
```python
MYSQL_ROOT_PASSWORD=changeme-root
MYSQL_DB_NAME=ong-empuje-comunitario
MYSQL_DB_USERNAME=changeme-user
MYSQL_DB_PASSWORD=changeme-pass

SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/ong_empuje_comunitario?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=${MYSQL_DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${MYSQL_DB_PASSWORD}

JWT_PRIVATE_KEY=changeme-jwt-key
SPRING_SECURITY_JWT_USER_GENERATOR=AUTH-SPRING-BACKEND

SPRING_MAIL_HOST=mailhog
SPRING_MAIL_PORT=1025

WSDL_URL=https://soap-app-latest.onrender.com/?wsdl
```
## 3. Levantar los servicios
Desde el directorio raíz del proyecto, ejecutar:
```bash
docker compose up --build
```
Esto levanta los siguientes contenedores:

* **db**: MySQL con la base de datos definida en el .env

* **grpc-server**: servidor principal Spring Boot con acceso a la base de datos y comunicación vía gRPC

* **grpc-client**: gateway NodeJS que expone métodos gRPC del **grpc-server** a través de comunicación vía HTTP

* **graphql-server**: servidor NodeJS + GraphQL

* **mailhog**: servidor SMTP falso para pruebas de correo

* **kafka**: servidor Kafka para comunicación asincrónica

* **kafbat-ui**: interfaz gráfica para interactuar con el servidor Kafka

* **rest-server**: servidor Spring Boot + Swagger que expone métodos REST para generar informes Excel de ciertos datos de la BD
## 4. Verificar servicios
A continuación se listan los puertos y rutas donde está corriendo cada uno de los servicios levantados con Docker.

* gRPC Server (Servidor principal con acceso a la base de datos):
http://localhost:9090

* gRPC Client (Gateway encargado de exponer los métodos gRPC del servidor principal):
http://localhost:8080

    A la hora de visualizar la pantalla del login, pueden iniciar sesión a través de un usuario que el servidor registra automáticamente en la base de datos:
    - ```Nombre de usuario``` | ```Correo electrónico```: Presidente | tomaslopez1987@gmail.com
    - ```Contraseña```: admin
    
    El sistema permite usar tanto el nombre de usuario como el correo electrónico para iniciar sesión.

<br>

* Kafka:
http://localhost:9092

* MySQL:
Puerto 3306. Usuario/clave los definidos en .env.

* GraphQL Server:
http://localhost:8080

* Mailhog (servidor):
http://localhost:1025

* Mailhog (UI web):
http://localhost:8025

* Kafbat UI (UI web de Kafka):
http://localhost:8999

* Rest Server (Swagger):
http://localhost:9093

## 🧪 Probar Mailhog

Mailhog captura todos los mails enviados desde la app.

* Entrar a http://localhost:8025

* Hacer una petición que envíe mail (ejemplo: crear un usuario).

* El mail aparecerá en la UI de Mailhog.

