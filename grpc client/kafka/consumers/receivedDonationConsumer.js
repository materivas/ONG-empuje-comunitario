const { Kafka } = require('kafkajs');
const { getListAsync, updateAsync, createAsync } = require('../../clients/inventoryClient');
const donationClient = require('../../clients/donationClient');
const clientId = "received-donation-consumer";
const brokers = ["kafka:9092"];

// El topic para las donaciones que RECIBIMOS (ID de nuestra organización = 1)
const topic = "transferencia-donaciones-1";

const kafka = new Kafka({ clientId, brokers });
const consumer = kafka.consumer({ groupId: clientId });

const consume = async () => {
    await consumer.connect();
    await consumer.subscribe({ topic, fromBeginning: true });
    console.log(`[Kafka] Consumidor de donaciones recibidas conectado y suscrito al topic: ${topic}`);

    await consumer.run({
        eachMessage: async ({ message }) => {
            try {
                const data = JSON.parse(message.value.toString());
                console.log(`[Kafka] Mensaje de donación recibido de la organización ID: ${data.id_organizacion_donante}`);

                const donationItems = data.lista_donaciones; // La lista de productos que nos donaron

                if (!donationItems || donationItems.length === 0) {
                    console.log("[Kafka] El mensaje no contenía items para donar. Omitiendo.");
                    return;
                }

                // 1. Obtenemos nuestro inventario actual para saber qué tenemos
                console.log("[gRPC] Obteniendo inventario actual...");
                const currentInventoryResponse = await getListAsync();
                const ourInventory = currentInventoryResponse.inventories || [];

                const currentDonationResponse = await getDonationListAsync();
                const ourDonations = currentDonationResponse.donations || [];
                console.log(ourDonations);

                // 2. Procesamos cada item donado
                for (const donatedItem of donationItems) {
                    // Buscamos si el item ya existe en nuestro inventario
                    const existingItem = ourInventory.find(inv =>
                        inv.category === donatedItem.categoria && inv.description === donatedItem.descripcion
                    );

                    const existingDonation = ourDonations.find(don =>
                        don.category === donatedItem.categoria && don.description === donatedItem.descripcion
                    );

                    if (existingItem) {
                        // --- SI YA EXISTE: Le sumamos la cantidad nueva ---
                        const newQuantity = existingItem.quantity + donatedItem.cantidad;
                        console.log(`[Lógica] Item existente: "${donatedItem.descripcion}". Actualizando stock: ${existingItem.quantity} + ${donatedItem.cantidad} = ${newQuantity}`);

                        const updateDto = {
                            idInventory: existingItem.idInventory,
                            // Pasamos los demás datos para que el DTO esté completo
                            category: existingItem.category,
                            description: existingItem.description,
                            quantity: newQuantity
                        };

                        await updateAsync(updateDto);
                        console.log(`[gRPC] Stock actualizado para "${donatedItem.descripcion}".`);

                    } else {
                        // --- SI NO EXISTE: Creamos un nuevo registro ---
                        console.log(`[Lógica] Item nuevo: "${donatedItem.descripcion}". Creando con stock: ${donatedItem.cantidad}.`);

                        const createDto = {
                            category: donatedItem.categoria,
                            description: donatedItem.descripcion,
                            quantity: donatedItem.cantidad
                        };

                        await createAsync(createDto);
                        console.log(`[gRPC] Nuevo item "${donatedItem.descripcion}" creado en el inventario.`);
                    }

                    if (existingDonation) {
                        const updateDto = {
                            idDonation: null,
                            category: existingDonation.category,
                            description: existingDonation.description,
                            quantity: donatedItem.cantidad,
                            isDeleted: false
                        };
                        await createDonationAsync(updateDto);
                    } else {
                        const updateDto = {
                            idDonation: null,
                            category: donatedItem.categoria,
                            description: donatedItem.descripcion,
                            quantity: donatedItem.cantidad,
                            isDeleted: false,
                            madeByOurselves: false
                        };
                        console.log("Voy a persistir esto: ", updateDto);
                        await createDonationAsync(updateDto);
                    }
                }
                console.log("--- Procesamiento de donación completado ---");
            } catch (error) {
                console.error('Error procesando el mensaje de Kafka o llamando a gRPC:', error);
            }
        },
    });
};

function getDonationListAsync() {
    return new Promise((resolve, reject) => {
        donationClient.GetDonationList({}, (error, response) => {
            if (error) return reject(error);
            resolve(response);
        });
    });
}

function createDonationAsync(dto) {
    return new Promise((resolve, reject) => {
        donationClient.CreateDonation(dto, (error, response) => {
            if (error) return reject(error);
            console.log(response);
            resolve(response);
        });
    });
}


module.exports = { consume };