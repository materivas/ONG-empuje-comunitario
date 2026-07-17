# Productor Kafka

Este script en Python permite enviar mensajes precargados a distintos topics de Kafka. Los datos se leen desde archivos `.json` y se publican en los siguientes topics:

- `solicitud-donaciones`
- `oferta-donaciones`
- `eventos-solidarios`

## Requisitos

- Python 3.10+
- Crear los topics necesarios desde el ui de kafka

## Cómo usar

Instalar la librería necesaria con:

```bash
pip install kafka-python
```

Para ejecutar el script, abrí una terminal en el directorio del productor y ejecutá el siguiente comando:

```bash
python producer.py
```

Se despleará el siguiente menú de opciones:

```bash
Producir mensajes:
1. Solicitudes de donaciones
2. Ofertas de donaciones
3. Eventos solidarios
4. Bajas de solicitudes
5. Bajas de eventos
6. Enviar todo y salir
7. Salir
```

Seleccioná la opción deseada para enviar los mensajes al topic correspondiente.
