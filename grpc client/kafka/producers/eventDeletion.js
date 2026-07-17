const { Kafka } = require('kafkajs')

const kafka = new Kafka({
  clientId: 'event-deletion-producer',
  brokers: ['kafka:9092'],
});

const producer = kafka.producer()

const deleteEvent = async (eventDeletion) => {
    await producer.connect();

    message = {
        event_id: eventDeletion.event_id,
        organization_id: eventDeletion.organization_id,
        event_name: eventDeletion.event_name,
        status: "DELETED",
        deleted_at: new Date().toISOString()
    };

    await producer.send({
        topic: 'baja-evento-solidario',
        messages: [
            { value: JSON.stringify(message) },
        ],
    });

    await producer.disconnect();
}

module.exports = {deleteEvent}