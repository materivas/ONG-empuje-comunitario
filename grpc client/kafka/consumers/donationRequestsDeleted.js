const { Kafka } = require("kafkajs");

// Configuración de Kafka
const kafka = new Kafka({
  clientId: "ong-app",
  brokers: ["kafka:9092"], // O también puede ser "localhost:29092"
});

// Consumer para bajas de solicitudes
const consumer = kafka.consumer({ groupId: `baja-solicitudes-group-${Date.now()}` });

// Lista de solicitudes eliminadas (se acumulan en memoria)
let deletedRequests = [];

// Inicia el consumer de Kafka y escucha el topic de bajas
const startDeletedRequestsConsumer = async () => {
  try {
    await consumer.connect();
    console.log("Consumer conectado a Kafka");

    await consumer.subscribe({
      topic: "baja-solicitud-donaciones",
      fromBeginning: true
    });

    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        try {
          const data = JSON.parse(message.value.toString());

          if (data?.request_id) {
            deletedRequests.push(data.request_id);
            console.log(`Solicitud dada de baja: ${data.request_id} (org: ${data.organization_id})`);
          } else {
            console.warn("Mensaje recibido sin request_id:", data);
          }
        } catch (err) {
          console.error("Error al procesar mensaje:", err);
        }
      },
    });
  } catch (err) {
    console.error("Error al iniciar consumer:", err);
  }
};

const deletedRequestsProducer = kafka.producer();

const publish = async (message) => {
  await deletedRequestsProducer.connect();
  await deletedRequestsProducer.send({
    topic: "baja-solicitud-donaciones",
    messages: [
      { value: JSON.stringify(message) }
    ]
  })
};


// Devuelve las solicitudes eliminadas almacenadas en memoria
const getDeletedRequests = () => deletedRequests;

module.exports = {
  startDeletedRequestsConsumer,
  getDeletedRequests,
  publish
};


