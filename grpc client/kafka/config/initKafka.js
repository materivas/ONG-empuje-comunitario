const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  clientId: "kafka-init",
  brokers: ["kafka:9092"],
});

const topicsToEnsure = [
  { topic: "solicitud-donaciones", numPartitions: 1, replicationFactor: 1 },
  { topic: "baja-evento-solidario", numPartitions: 1, replicationFactor: 1 },
  { topic: "baja-solicitud-donaciones", numPartitions: 1, replicationFactor: 1 },
  { topic: "eventos-solidarios", numPartitions: 1, replicationFactor: 1 },
  { topic: "oferta-donaciones", numPartitions: 1, replicationFactor: 1 },
  { topic: "transferencia-donaciones-1", numPartitions: 1, replicationFactor: 1 },
];

const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const initKafka = async () => {
  const admin = kafka.admin();
  await admin.connect();

  const existing = await admin.listTopics();
  const newTopics = topicsToEnsure.filter((t) => !existing.includes(t.topic));

  if (newTopics.length > 0) {
    await admin.createTopics({
      topics: newTopics,
      waitForLeaders: true,
      timeout: 10000,
    });
  }

  await admin.disconnect();
  await wait(10000);
};

initKafka()
  .catch((err) => {
    console.error("Error al inicializar Kafka:", err);
    process.exit(1);
  }
);

module.exports = initKafka;