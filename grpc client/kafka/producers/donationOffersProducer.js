const { Kafka, Partitioners } = require('kafkajs');

const kafka = new Kafka({
    clientId: 'donation-offer-producer',
    brokers: ['kafka:9092']
});

const producer = kafka.producer({
    createPartitioner: Partitioners.DefaultPartitioner
});

const sendOffer = async (offerMessage) => {
    const maxRetries = 5;
    const retryDelay = 3000; // 3 segundos

    for (let i = 0; i < maxRetries; i++) {
        try {
            console.log(`Intento ${i + 1} de conectar y enviar a Kafka...`);
            await producer.connect();
            await producer.send({
                topic: 'oferta-donaciones',
                messages: [{ value: JSON.stringify(offerMessage) }],
            });
            console.log('Mensaje de oferta de donación enviado con éxito:', offerMessage);
            await producer.disconnect();
            return; // Salimos de la función si tiene éxito
        } catch (error) {
            console.error(`Intento ${i + 1} falló:`, error.message);
            await producer.disconnect(); // Nos aseguramos de desconectar antes de reintentar
            if (i < maxRetries - 1) {
                console.log(`Reintentando en ${retryDelay / 1000} segundos...`);
                await new Promise(res => setTimeout(res, retryDelay));
            } else {
                console.error('No se pudo enviar el mensaje después de varios intentos.');
                throw error; // Relanzar el error final si todos los intentos fallan
            }
        }
    }
};

module.exports = { sendOffer };