const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');

// Ubicación del archivo .proto
const PROTO_PATH = path.join(__dirname, '../proto/donation.proto');

// carga del archivo .proto en un solo paquete
const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true,
});

// servicio
const proto = grpc.loadPackageDefinition(packageDefinition);

// crear cliente
const donationClient = new proto.DonationService('grpc_server:9090', grpc.credentials.createInsecure());

// exportar el cliente
module.exports = donationClient;