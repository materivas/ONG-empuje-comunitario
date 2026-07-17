const express = require('express');
const router = express.Router();
const jwtAuth = require('../auth/jwt-auth');
const { getOffers } = require('../../kafka/consumers/donationOffers');
const { sendOffer } = require('../../kafka/producers/donationOffersProducer');
const { v4: uuidv4 } = require('uuid'); // Para generar IDs únicos

// --- VISTAS (GET) ---

// Ruta para mostrar la lista de ofertas externas
router.get('/', jwtAuth, (req, res) => {
    res.render('donations/offers', {
        title: 'Ofertas de Donaciones Externas',
        roles: req.user.roles
    });
});

// Ruta para mostrar el formulario de creación de oferta
router.get('/create', jwtAuth, (req, res) => {
    res.render('donations/createDonationOffer', {
        title: 'Ofrecer una Donación',
        roles: req.user.roles
    });
});


// --- API (GET y POST) ---

// API para que el frontend obtenga la lista de ofertas en JSON
router.get('/api/list', jwtAuth, (req, res) => {
    const offers = getOffers();
    res.json(offers);
});

// API para recibir los datos del formulario y producir el mensaje en Kafka
router.post('/api/create', jwtAuth, async (req, res) => {
    try {
        const { donations } = req.body;

        if (!donations || donations.length === 0) {
            return res.status(400).json({ success: false, message: 'La lista de donaciones no puede estar vacía.' });
        }

        const offerMessage = {
            ID_oferta: `OFERTA-${uuidv4().substring(0, 8).toUpperCase()}`,
            ID_organizacion_donante: req.user.username || 'empuje-comunitario',
            Lista_donaciones_ofrecidas: donations
        };

        await sendOffer(offerMessage);

        res.status(201).json({ success: true, message: 'Oferta de donación publicada correctamente.' });

    } catch (error) {
        console.error('Error en el endpoint /api/create:', error);
        res.status(500).json({ success: false, message: 'Error interno del servidor al publicar en Kafka.' });
    }
});

module.exports = router;