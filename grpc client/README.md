# gRPC Client

Este documento describe los endpoints HTTP del cliente gRPC.

## Pre-requisitos

Si se levanta la aplicación a través de Docker, no es necesario realizar ningún tipo de configuración en esta aplicación.

En caso de desear levantar esta aplicación sin Docker, crear una copia del archivo `.env-example` que tenga el nombre `.env` y setear la variable de entorno indicada en dicho archivo.

## Tecnologías
- [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
- [NodeJS](https://nodejs.org/es)
- [KafkaJS](https://kafka.js.org/docs/getting-started)

## Dependencias
- [@grpc/grpc-js](https://www.npmjs.com/package/@grpc/grpc-js)
- [express](https://www.npmjs.com/package/express)
- [cors](https://www.npmjs.com/package/cors)
- [express-jwt](https://www.npmjs.com/package/express-jwt)
- [cookie-parser](https://www.npmjs.com/package/cookie-parser)
- [dotenv](https://www.npmjs.com/package/dotenv)

## Funcionalidades

Este cliente gRPC cuenta con:
- Comunicación con un server gRPC.
- Routers HTTP que invocan métodos gRPC de las siguientes entidades del server:
  - Event
  - EventInventory
  - Inventory
  - User
- Routers HTTP que renderizan vistas EJS.
- Consumo de los siguientes servicios Protobuf:
  - event.proto
  - inventory.proto
  - user.proto
  - utils.proto
- Implementación de express-jwt para validar tokens del frontend en caso de ejecutar una request que requiera autenticación.
- Implementación de producers y consumers para Kafka, para abrir nuevos canales de comunicación de este sistema con otros externos.

# Endpoints

## General

| Endpoint | Respuesta |
|----------|--------------|
| `GET /` | Devuelve la vista del login |
| `GET /home` | Devuelve la vista de la home |
| `GET /about` | Devuelve la vista de la sección "Sobre Nosotros" |
| `GET /contact` | Devuelve la vista de la sección "Contáctanos" |
| `GET /privacy` | Devuelve la vista de la sección "Política de privacidad" |


## User Router

### Endpoints de vistas

| Endpoint | Respuesta |
|----------|--------------|
| `GET /user` | Devuelve una vista que muestra a todos los usuarios |
| `GET /user/create` | Devuelve una vista que permite crear usuarios |
| `GET /user/modify/{username}` | Devuelve una vista para modificar al usuario pasado a través del parámetro `username` de la URL |

### Endpoints de la API

| Endpoint | Body de la request | Respuesta |
|----------|--------------|--------------|
| `POST /user/login` | Enviar `username` y `password`. | Devuelve todos los datos del usuario y un JWT Token generado por el server. El JWT Token es guardado en las cookies del usuario. |
| `POST /user/logout` | No espera ningún parámetro. | Elimina la cookie donde se había guardado el JWT Token del usuario. |
| `POST /user/create` | Enviar `username`, `name`, `surname`, `phoneNumber`, `email`, `isActive`, `roles`. | Devuelve un boolean `succeeded` junto a una string `message` indicando si la creación fue exitosa o no. |
| `POST /user/modify` | Enviar `id`, `username`, `name`, `surname`, `phoneNumber`, `email`, `isActive`, `roles`. | Devuelve un boolean `succeeded` junto a un `message` indicando si la modificación fue exitosa o no. |
| `POST /user/delete` | Enviar `id`. | Devuelve un boolean `succeeded` junto a una string `message` indicando si la eliminación fue exitosa o no. |
| `GET /user/list` | No espera ningún parámetro. | Devuelve una lista cargada con todos los usuarios de la base de datos. |
| `GET /user/active-list` | No espera ningún parámetro. | Devuelve una lista cargada con todos los usuarios de la base de datos que tengan su atributo `isActive` seteado a TRUE. |
| `GET /user/{username}` | No espera ningún parámetro. | Devuelve al usuario de la base de datos que posea el nombre de usuario pasado a través de la URL. |

## Event Router

### Endpoints de vistas

| Endpoint | Respuesta |
|----------|--------------|
| `GET /events` | Devuelve una vista que muestra todos los eventos. |
| `GET /events/edit/{id}` | Devuelve una vista que permite modificar el evento con el id enviado como parámetro en la URL. |
| `GET /events/create` | Devuelve una vista que permite crear un evento nuevo. |
| `GET /donationsRegistry/{id}/{name}` | Devuelve una vista que muestra el registro de donaciones del evento con el id y el nombre enviados como parámetro en la URL. |

### Endpoints de la API

| Endpoint | Body de la request | Respuesta |
|----------|--------------|--------------|
| `GET /events/getEvent/{id}` | Enviar `id`. | Devuelve todos los datos del evento con el id enviado por parámetro en la URL. | 
| `GET /events/getEventsWithParticipants` | No requiere parámetros. | Devuelve una lista con todos los eventos con sus respectivos participantes. | 
| `GET /events/getEventsWithoutParticipants/{id}` | Enviar `id` | Devuelve una lista con todos los eventos sin participantes. Cada evento está acompañado de un boolean que indica si el usuario con el id enviado por parámetro en la URL forma parte del mismo. | 
| `GET /events/getEventInventory/{id}` | Enviar `id`. | Devuelve la lista de donaciones del evento con el id enviado como parámetro en la URL. | 
| `POST /events/create` | Enviar `name`, `description`, `date`, `participants[]`. | Devuelve un boolean `succeeded` junto a una string `message` indicando si la creación del evento fue exitosa o no. | 
| `PUT /events/modifyEvent` | Enviar `id`, `name`, `description`, `date`, `participants[]`, `is_completed` | Devuelve un boolean `succeeded` junto a una string `message` indicando si la modificación del evento fue exitosa o no. | 
| `DELETE /events/deleteEvent/{id}` | Enviar `id` | Devuelve un boolean `succeeded` junto a una string `message` indicando si la eliminación fue exitosa o no. |
| `POST /events/assignUserToEvent` | Enviar `event_id`, `user_id`. | Devuelve un boolean `succeeded` junto a una string `message` indicando si el alta del usuario al evento fue exitoso o no. | 
| `POST /events/deleteUserFromEvent` | Enviar `event_id`, `user_id`. | Devuelve un boolean `succeeded` junto a una string `message` indicando si la baja del usuario al evento fue exitosa o no. |
| `POST /events/registerEventInventory` | Enviar `user_id`, `event_id`, `inventory_id`, `quantity`. | Devuelve un boolean `succeeded` junto a una string `message` indicando el registro de donaciones fue exitoso o no. |

## Inventory Router

### Endpoints de vistas

| Endpoint | Respuesta |
|----------|--------------|
| `GET /` | a |

### Endpoints de la API

| Endpoint | Body de la request | Respuesta |
|----------|--------------|--------------|
| `GET /` | a | a | 

# Endpoints de Kafka

## Donation Requests Router

### Endpoints de vistas

| Endpoint           | Descripción                                                                    | Restricciones de acceso            | Respuesta                                                                                      |
| ------------------ | ------------------------------------------------------------------------------ | ---------------------------------- | ---------------------------------------------------------------------------------------------- |
| `GET /allRequests` | Renderiza la vista con todas las solicitudes de donación.                      | Solo roles `PRESIDENTE` o `VOCAL`. | Renderiza la vista `donations/donationRequests` o `error/error-403` si no tiene permisos.      |
| `GET /ourRequests` | Renderiza la vista con las solicitudes de donación propias de la organización. | Solo roles `PRESIDENTE` o `VOCAL`. | Renderiza la vista `donations/ourDonationRequests` o `error/error-403` si no tiene permisos.   |
| `GET /create`      | Renderiza la vista para crear una nueva solicitud de donación.                 | Solo roles `PRESIDENTE` o `VOCAL`. | Renderiza la vista `donations/createDonationRequest` o `error/error-403` si no tiene permisos. |


### Endpoints de la API

| Endpoint  | Método | Body de la request                                                                   | Descripción                                                                            | Respuesta                                                                                                                                                                                                     |
| --------- | ------ | ------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/all`    | `GET`  | —                                                                                    | Obtiene todas las solicitudes de donación **excepto** las propias de la organización.  | `{ allDonationRequests: [...] }`                                                                                                                                                                              |
| `/ours`   | `GET`  | —                                                                                    | Obtiene todas las solicitudes de donación pertenecientes a **nuestra organización**.   | `{ ourDonationRequests: [...] }`                                                                                                                                                                              |
| `/create` | `POST` | `json { "donations": [ ... ] } `                                                     | Crea una nueva solicitud de donación y la publica en Kafka.                            | **Éxito:** `{ succeeded: true, message: "Donación publicada correctamente." }`<br>**Error:** `{ succeeded: false, message: "No se pudo publicar la solicitud de donación. Vuelva a intentarlo, por favor." }` |
| `/delete` | `POST` | `json { "organizationId": number, "requestId": number, "deleted_at": "timestamp" } ` | Elimina (marca como eliminada) una solicitud de donación y publica el evento en Kafka. | **Éxito:** `{ succeeded: true, message: "Donación eliminada correctamente." }`<br>**Error:** `{ succeeded: false, message: "No se pudo eliminar la solicitud de donación. Vuelva a intentarlo, por favor." }` |

## Donation Transfer Router

### Endpoints de vistas

| Endpoint                      | Descripción                                                        | Restricciones de acceso            | Respuesta                                                                                                              |
| ----------------------------- | ------------------------------------------------------------------ | ---------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| `GET /create/:organizationId` | Renderiza la vista para transferir donaciones a otra organización. | Solo roles `PRESIDENTE` o `VOCAL`. | Renderiza la vista `donations/transferDonation` pasando `{ organizationId }` o `error/error-403` si no tiene permisos. |

### Endpoints de la API

| Endpoint  | Método | Body de la request                                                                                                 | Descripción                                                                                                                                                                    | Respuesta                                                                                                                                                                                                                                                                                                                                                                                                              |
| --------- | ------ | ------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/create` | `POST` | `json { "organizationId": number, "donations": [ { "id": number, "description": string, "quantity": number } ] } ` | Transfiere las donaciones indicadas a otra organización. Verifica que el inventario tenga suficiente cantidad y actualiza la base antes de publicar la transferencia en Kafka. | **Éxito:** `{ succeeded: true, message: "Donación transferida correctamente." }`<br>**Error por inventario insuficiente:** `{ succeeded: false, message: "Fallo al transferir la donación. El inventario de ... no tiene la cantidad suficiente como para transferir la cantidad indicada." }`<br>**Error general:** `{ succeeded: false, message: "Fallo al transferir la donación. Intentar de nuevo, por favor." }` |
