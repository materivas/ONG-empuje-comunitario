const { Kafka } = require('kafkajs')

const deletedEventsConsumer = require('./solidarityEventsDeleted');

const kafka = new Kafka({
  clientId: 'external-events-consumer',
  brokers: ['kafka:9092'],
});

const events = [];

const consumer = kafka.consumer({ 
  // para un group id unico
  groupId: `external-events-${Date.now()}` 
});

const startEventsConsumer = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: 'eventos-solidarios', fromBeginning: true });

  await consumer.run({
    //guarda los mensajes en una lista
    eachMessage: async ({ topic, partition, message }) => {
      const event = JSON.parse(message.value.toString());
      if (event) {
        events.push(event);
      }
    },
  });
}

const getExternalEvents = () => events;

const getActiveExternalEvents = () =>{
  //guardo la lista de ids de eventos eliminados
  const deletedIds = deletedEventsConsumer.getDeletedEvents();
  //filtro los eventos para devolver solo los que no están eliminados
  return events.filter(event => !deletedIds.includes(event.event_id));
}

// se exportan las funciones para iniciar el consumidor y para obtener los eventos
module.exports ={
  startEventsConsumer,
  getExternalEvents,
  getActiveExternalEvents
}