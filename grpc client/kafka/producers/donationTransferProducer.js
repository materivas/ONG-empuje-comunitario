const { Kafka } = require('kafkajs');
const clientId = "donation-transfer-producer";
const broker = "kafka:9092";
const groupId = `donation-transfer-${Date.now()}`;
const topicName = "transferencia-donaciones-";
const donationTransferConsumer = require("../consumers/donationTransferConsumer");

const transferDonation = async (organizationId, donationTransferOrganizationId, donations) => {
    const kafka = new Kafka({
        clientId: clientId,
        brokers: [broker],
    });
    const topic = topicName+organizationId;

    //previo a enviar el mensaje, me aseguro que el topic exista
    const admin = kafka.admin();
    await admin.connect();
    const existingTopics = await admin.listTopics();
    let doesntExists = true;
    existingTopics.forEach(existingTopic => {
        if(topic == existingTopic) {
            doesntExists = false;
        }
    });
    //en caso de que no exista, lo creo
    if(doesntExists) {
        await admin.createTopics({
            validateOnly: false,
            waitForLeaders: true,
            timeout: 5000,
            topics: [{
                topic: topic,
                numPartitions: 1,
                replicationFactor: 1,
                replicaAssignment: [],
                configEntries: []
            }]
        });
    }
    await admin.disconnect();
    const requestId = await donationTransferConsumer.computeNewLastRequestId(organizationId, topic);

    const producer = kafka.producer();
    await producer.connect();
    await producer.send({
        topic: topic,
        messages: [
        {
            value: JSON.stringify({
            request_id: requestId,
            organization_id: donationTransferOrganizationId,
            donations: donations,
            }),
        },
        ],
    });

    await producer.disconnect();
};


module.exports = { transferDonation };