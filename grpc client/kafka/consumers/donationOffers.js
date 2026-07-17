const { Kafka } = require('kafkajs');

const offersCache = [];

const kafka = new Kafka({
    clientId: 'donation-offers-consumer',
    brokers: ['kafka:9092']
});

const consumer = kafka.consumer({ groupId: 'offers-group' });

const runDonationOffersConsumer = async () => {
    await consumer.connect();
    await consumer.subscribe({ topic: 'oferta-donaciones', fromBeginning: true });

    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            console.log('Nueva oferta de donación recibida:');
            try {
                const offer = JSON.parse(message.value.toString());
                
                const existingOfferIndex = offersCache.findIndex(o => o.ID_oferta === offer.ID_oferta);
                if (existingOfferIndex > -1) {
                    offersCache[existingOfferIndex] = offer;
                } else {
                    offersCache.push(offer);
                }
                console.log(offersCache);
            } catch (error) {
                console.error("Error al procesar mensaje de oferta:", error);
            }
        },
    });
};

const getOffers = () => {
    return offersCache;
};

module.exports = { runDonationOffersConsumer, getOffers };