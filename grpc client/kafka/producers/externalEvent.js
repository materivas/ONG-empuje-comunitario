const { Kafka } = require('kafkajs')

const kafka = new Kafka({
  clientId: 'event-producer',
  brokers: ['kafka:9092'],
});

const producer = kafka.producer()

const createEvent = async (event) => {
    await producer.connect();

    message = {
        organization_id: event.organization_id,
	    event_id: event.event_id,
	    event_name: event.event_name,
	    description: event.description,
	    date: event.date
    };

    await producer.send({
        topic: 'eventos-solidarios',
        messages: [
            { value: JSON.stringify(message) },
        ],
    });

    await producer.disconnect();
}

module.exports = {createEvent}