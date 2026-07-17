const { Kafka } = require('kafkajs')

const kafka = new Kafka({
  clientId: 'event-adhesion-producer',
  brokers: ['kafka:9092'],
});

const producer = kafka.producer()

const sendEventAdhesion = async (eventAdhesion) => {
    await producer.connect();

    message = {
        event_id: eventAdhesion.event_id,
        voluntary: eventAdhesion.voluntary,
    };

    await producer.send({
        topic: 'adhesion-evento.ong' + eventAdhesion.organization_id,
        messages: [
            { value: JSON.stringify(message) },
        ],
    });

    await producer.disconnect();
}

module.exports = {sendEventAdhesion}