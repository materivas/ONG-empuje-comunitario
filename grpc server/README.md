# gRPC Server

Este documento describe los servicios gRPC disponibles, sus métodos, parámetros y respuestas. Además de breves explicaciones sobre ciertas funcionalidades.

## Tecnologías
- [Java 17](https://docs.oracle.com/en/java/javase/17/docs/api/index.html)
- [Spring Boot 3.5.5](https://spring.io/blog/2025/08/21/spring-boot-3-5-5-available-now)

## Dependencias
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/index.html)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Spring gRPC](https://docs.spring.io/spring-grpc/reference/getting-started.html)
- [Spring Boot Starter Mail](https://docs.spring.io/spring-boot/reference/io/email.html)
- [Auth Java JWT](https://github.com/auth0/java-jwt)
- [Lombok](https://projectlombok.org/)
- [MySQL Connector](https://dev.mysql.com/doc/connector-j/en/)
- [Google Protobuf](https://protobuf.dev/)

## Funcionalidades

Este servidor gRPC cuenta con:
- Acceso a una base de datos MySQL.
- Repositorios Spring Data JPA sobre las entidades:
  - Event
  - EventInventory
  - Inventory
  - Role
  - User
- Implementaciones de los siguientes servicios Protobuf:
  - event.proto
  - inventory.proto
  - user.proto
  - utils.proto
- Implementación de ServerInterceptor para validar requests entrantes.
- Implementación de la librería JavaMailSender para enviar correos a usuarios.
- Implementación de Auth Java JWT para generar, firmar y validar tokens.

# Server Interceptor

Cada request realizada al servidor es interceptada por la clase ```GrpcServerInterceptor```, contenida en el paquete ```configuration/security```.

La clase ```GrpcServerInterceptor``` declara en HashMaps:
- **Métodos públicos**: ejecuta la request sin realizar ninguna validación.
- **Métodos privados**: ejecuta la request sí y solo sí el server valida lo siguiente. En caso de que una validación falle, rechaza la request:
  - Extrae el JWT Token de la request y verifica que sea válido.
  - Extrae los roles del usuario contenido en los claims del JWT Token y valida que al menos uno de sus roles tenga el permiso suficiente para ejecutar el método gRPC de su request.

# Servicios Protobuf

## Métodos de UserService

- **UserService/Login**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "username": "nombreDeUsuarioOEmail",
  "password": "contraseña"
}
```

**Validaciones**:
- Si el atributo ```username``` no existe en la base de datos, el server devuelve un Status NOT_FOUND.
- Si el atributo ```username``` existe en la base de datos, pero ```password``` no, el server devuelve un Status INVALID_ARGUMENT.
- Si los atributos ```username``` y ```password``` existen en la base de datos, pero en la base de datos el usuario tiene su atributo ```isActive``` en FALSE, el server devuelve un Status FAILED_PRECONDITION.

Si el usuario no levanta ninguna de las anteriores excepciones, el server crea un JWT Token para él y se lo devuelve en una response junto a todos sus datos.

- **UserService/CreateUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "username": "nombreDeUsuario",
  "name": "nombre",
  "surname": "apellido",
  "phoneNumber": "+00 00 0000-0000",
  "email": "correo@electronico.com",
  "isActive": "trueOFalse",
  "roles": [
    {"name": "primerRol"},
    {"name":  "segundoRol"}
  ]
}
```

El usuario puede tener tantos roles existan.

**Validaciones**:
- Si el atributo ```username``` es una String vacía, el server no realiza la creación.
- Si el atributo ```name``` es una String vacía, el server no realiza la creación.
- Si el atributo ```surname``` es una String vacía, el server no realiza la creación.
- Si el atributo ```phoneNumber``` NO es una String vacía y no es un número de telefono con formato válido, el server no realiza la creación.
- Si el atributo ```email``` es una String vacía o su formato no es válido, el server no realiza la creación.
- Si el atributo ```roles``` no contiene ningún objeto en su interior, el server no realiza la creación.
- Si los atributos ```username```, ```phoneNumber```, ```email``` ya fueron utilizados en otro usuario registrado en la base de datos, el server no realiza la creación.

Si el objeto no levanta ninguna de las anteriores validaciones, se inserta en la base de datos.

- **UserService/ModifyUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "id": "1",
  "userWithRolesDTO": {
    "username": "nombreDeUsuario",
    "name": "nombre",
    "surname": "apellido",
    "phoneNumber": "+00 00 0000-0000",
    "email": "correo@electronico.com",
    "isActive": "trueOFalse",
    "roles": [
      {"name": "primerRol"},
      {"name":  "segundoRol"}
    ]
  }
}
```

El usuario puede tener tantos roles existan.

**Validaciones**:
- Si el atributo ```id``` no está siendo utilizado por ningún usuario en la base de datos, el server no realiza la modificación.
- Si el atributo ```username``` es una String vacía, el server no realiza la modificación.
- Si el atributo ```name``` es una String vacía, el server no realiza la modificación.
- Si el atributo ```surname``` es una String vacía, el server no realiza la modificación.
- Si el atributo ```phoneNumber``` NO es una String vacía y no es un número de telefono con formato válido, el server no realiza la modificación.
- Si el atributo ```email``` es una String vacía o su formato no es válido, el server no realiza la modificación.
- Si el atributo ```roles``` no contiene ningún objeto en su interior, el server no realiza la modificación.
- Si los atributos ```username```, ```phoneNumber```, ```email``` ya fueron utilizados en otro usuario registrado en la base de datos, el server no realiza la modificación.

Si el objeto no levanta ninguna de las anteriores validaciones, se modifica en la base de datos. Con el agregado de que si su atributo ```isActive``` es seteado a FALSE, se lo elimina de todos los eventos en los cuales estaba registrado.

- **UserService/DeleteUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "id": "1"
}
```

**Validaciones**:
- Si el atributo ```id``` no está siendo utilizado por ningún usuario en la base de datos, el server no lo elimina.

Si el objeto no levanta la validación, en la base de datos se setea su atributo ```isActive``` a FALSE (baja lógica) y se lo elimina de todos los eventos en los cuales estaba registrado.

- **UserService/GetUserList**:

Recibe por parámetro un objeto vacío:

```json
{
  
}
```

El server recupera todos los usuarios de la base de datos y los devuelve en su response.

- **UserService/GetActiveUserList**:

Recibe por parámetro un objeto vacío:

```json
{
  
}
```

El server recupera todos los usuarios de la base de datos que tengan su atributo ```isActive``` seteado a TRUE y los devuelve en su response.

- **UserService/GetUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "username": "nombreDeUsuario"
}
```

**Validaciones**:
- Si en la base de datos no se encuentra ningún usuario con el valor del atributo ```username```, el server responde con un Status NOT_FOUND.

Si el objeto no levanta la validación, el server devuelve al usuario solicitado junto a todos sus datos en su response.

## Métodos de EventService

- **EventService/CreateEvent**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "date": {
        "nanos": "milisegundos",
        "seconds": "segundos"
    },
    "description": "descripcion",
    "name": "nombre",
    "participants": [
        {
            "id": "1",
            "username": "nombreUsuario1"
        }
        {
            "id": "2",
            "username": "nombreUsuario2"
        }
    ]
}
```

**Validaciones**:
- Si el atributo ```date``` es anterior a hoy, el server devuelve un Status INVALID_ARGUMENT.

Si el objeto no levanta ninguna de las anteriores excepciones, se crea en la base de datos.

- **EventService/ModifyEvent**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "date": {
        "nanos": "milisegundos",
        "seconds": "segundos"
    },
    "description": "descripcion",
    "id": "1",
    "is_completed": "trueOrFalse",
    "name": "nombre",
    "participants": [
        {
            "id": "1",
            "username": "nombreUsuario1"
        },
        {
            "id": "2",
            "username": "nombreUsuario2"
        }
    ]
}
```

**Validaciones**:
- Si no se encuentra un evento con el ```id``` enviado, el server devuelve un Status NOT_FOUND.
- Si el atributo ```date``` es anterior a hoy, el server devuelve un Status INVALID_ARGUMENT y no se modifica el evento.

Si el objeto no levanta ninguna de las anteriores excepciones, se modifica en la base de datos.

- **EventService/DeleteEvent**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "id": "1"
}
```

**Validaciones**:
- Si no se encuentra un evento con el ```id``` enviado, el server devuelve un Status NOT_FOUND.
- Si el evento a eliminar posee el atributo ```isCompleted``` en TRUE, no se elimina de la base de datos y el server devuelve un Status FAILED_PRECONDITION.

Si el objeto no levanta ninguna de las anteriores excepciones, se elimina de la base de datos.

- **EventService/AssignUserToEvent**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "event_id": "1",
    "user_id": "1"
}
```

**Validaciones**:
- Si no se encuentra un evento con el id = ```event_id``` enviado, el server devuelve un Status NOT_FOUND.
- Si no se encuentra un usuario con el id = ```user_id``` enviado, el server devuelve un Status NOT_FOUND.

Si el objeto no levanta ninguna de las anteriores excepciones, se agrega al usuario al evento y se guarda la modificación en la base de datos.

- **EventService/DeleteUserFromEvent**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "event_id": "1",
    "user_id": "1"
}
```

**Validaciones**:
- Si no se encuentra un evento con el id = ```event_id``` enviado, el server devuelve un Status NOT_FOUND.
- Si no se encuentra un usuario con el id = ```user_id``` enviado, el server devuelve un Status NOT_FOUND.

Si el objeto no levanta ninguna de las anteriores excepciones, se elimina al usuario del evento y se guarda la modificación en la base de datos.

- **EventService/GetEvent**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "id": "1"
}
```

**Validaciones**:
- Si no se encuentra un evento con el id = ```event_id``` enviado, el server devuelve un Status NOT_FOUND.

Si el objeto no levanta ninguna de las anteriores excepciones, el server devuelve el evento con todos sus participantes.

- **EventService/GetEventsWithParticipantsList**:

Recibe por parámetro un objeto vacío:

```json
{
  
}
```

El server devuelve una lista con todos los eventos con sus respectivos participantes de la base de datos.

- **EventService/GetEventsWithoutParticipantsList**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "id": "1"
}
```

El server devuelve una lista con todos los eventos con sin participantes de la base de datos. Con el agregado de que cada evento cuenta con un booleano que se setea en TRUE si el atributo ```id``` coincide con uno de sus participantes.

- **EventService/RegisterEventInventory**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "event_id": "1",
    "inventory_id": "1",
    "quantity": 30,
    "user_id": "1"
}
```

**Validaciones**:
- Si no se encuentra un evento con el id = ```event_id``` enviado, el server devuelve un Status NOT_FOUND.
- Si no se encuentra un usuario con el id = ```user_id``` enviado, el server devuelve un Status NOT_FOUND.
- Si no se encuentra un inventario con el id = ```inventory_id``` enviado, el server devuelve un Status NOT_FOUND.
- Si el evento con el id = ```event_id``` posee el atributo ```isCompleted``` en TRUE (ya finalizó) , el server devuelve un Status FAILED_PRECONDITION.
- Si el atributo ```quantity``` es menor a 0, el server devuelve un Status INVALID_ARGUMENT.
- Si el atributo ```quantity``` es mayor al stock disponible del inventario con id = ```inventory_id```, el server devuelve un Status INVALID_ARGUMENT.
- Si el inventario con el id = ```inventory_id``` posee el atributo ```isDeleted``` en TRUE (se eliminó) , el server devuelve un Status INVALID_ARGUMENT.

Si el objeto no levanta ninguna de las anteriores excepciones, se registra la donación en la base de datos y se descuenta el stock del inventario.

- **EventService/GetEventInventory**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
    "id": "1"
}
```

**Validaciones**:
- Si no se encuentra un evento con el id = ```event_id``` enviado, el server devuelve un Status NOT_FOUND.

Si el objeto no levanta ninguna de las anteriores excepciones, el server devuelve una lista con todas las donaciones del evento en la base de datos.

## Métodos de InventoryService

- a
