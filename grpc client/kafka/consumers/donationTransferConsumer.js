const { Kafka } = require('kafkajs');
const clientId = "donation-transfer-consumer";
const broker = "kafka:9092";

//pool de conexiones
const connections = new Map();

//aca generamos una conexion nueva al broker y la metemos a la pool; solo se crea una conexion por topic
const createConsumer = async (topic, organizationId) => {
    const kafka = new Kafka({ clientId, brokers: [broker] });
    const admin = kafka.admin();
    const consumer = kafka.consumer({ groupId: `id-checker-${organizationId}-${Date.now()}` });

    await admin.connect();
    await consumer.connect();
    await consumer.subscribe({ topic, fromBeginning: true });

    const state = { lastRequestId: 1 };

    await consumer.run({
        eachMessage: async ({ message }) => {
            const data = JSON.parse(message.value.toString());
            if (data.request_id >= state.lastRequestId) {
                state.lastRequestId = data.request_id + 1;
            }
        },
    });

    connections.set(topic, { admin, consumer, state });
    return { admin, consumer, getLastRequestId: () => state.lastRequestId };
};

const computeNewLastRequestId = async (organizationId, topic) => {
    if (connections.has(topic)) {
        return connections.get(topic).state.lastRequestId;
    }
    const consumer = await createConsumer(topic, organizationId);
    return consumer.getLastRequestId();
};

module.exports = { computeNewLastRequestId };