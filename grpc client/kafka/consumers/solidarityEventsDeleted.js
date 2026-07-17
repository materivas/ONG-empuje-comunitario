import { Kafka } from "kafkajs";

const kafka = new Kafka({
  clientId: "ong-app",
  brokers: ["kafka:9092"], // O también puede ser "localhost:29092" 
});

// Consumer para bajas de eventos solidarios
const consumer = kafka.consumer({ groupId: "baja-eventos-group" });

// Array para almacenar eventos eliminados en memoria
let deletedEvents = [];

//Inicia el consumer de Kafka para bajas de eventos solidarios
const startDeletedEventsConsumer = async () => {
  try {
    await consumer.connect();
    console.log("Consumer de bajas de eventos conectado a Kafka");

    await consumer.subscribe({
      topic: "baja-evento-solidario",
      fromBeginning: false, // solo consume lo nuevo
    });

    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        try {
          const data = JSON.parse(message.value.toString());

          if (data?.event_id) {
            deletedEvents.push(data.event_id);
            console.log(`Evento eliminado: ${data.event_id} (org: ${data.organization_id})`);
          } else {
            console.warn("Mensaje recibido sin event_id:", data);
          }
        } catch (err) {
          console.error("Error al procesar mensaje:", err);
        }
      },
    });
  } catch (err) {
    console.error("Error al iniciar consumer de bajas de eventos:", err);
  }
};


// Devuelve los eventos eliminados almacenados en memoria
const getDeletedEvents = () => deletedEvents;

export { startDeletedEventsConsumer, getDeletedEvents };


console.log("Consumer de bajas de eventos iniciado");
