const express = require('express');
const router = express.Router();
const jwtAuth = require("../auth/jwt-auth");

const {
    getListAsync,
    getByIdAsync,
    createAsync,
    updateAsync,
    deleteAsync,
    getAvailableAsync
} = require('../../clients/inventoryClient');

// Middleware para pasar los roles a todas las vistas
router.use((req, res, next) => {
    const roles = req.user ? req.user.roles : []; 
    res.locals.roles = roles;
    next();
});

// Rutas de Vistas (GET)
router.get('/', jwtAuth, async (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")){
        res.render("error/error-403");
        return;
    }
    try {
        const response = await getListAsync();
        res.render('inventories/list', { inventories: response.inventories || [] });
    } catch (err) {
        console.error('Error al obtener la lista de inventarios:', err);
        res.status(500).send('Error al obtener la lista de inventarios.');
    }
});

router.get('/create', jwtAuth,(req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")){
        res.render("error/error-403");
        return;
    }
    res.render('inventories/createInventory');
});

router.get('/:id/edit', jwtAuth, async (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")){
        res.render("error/error-403");
        return;
    }
    try {
        const id = req.params.id;
        const response = await getByIdAsync(id);
        res.render('inventories/editInventory', { inventory: response });
    } catch (err) {
        console.error('Error al obtener el inventario para editar:', err);
        res.status(404).send('Inventario no encontrado.');
    }
});


router.get('/available', async (req, res) => {
    try {
        const response = await getAvailableAsync();
        const inventories = (response && response.inventories) ? response.inventories : [];

        // Mapear a formato simple pedido por tu compañero
        const simplified = inventories.map(inv => ({
            id: Number(inv.idInventory || 0),
            description: inv.description || '',
            quantity: Number(inv.quantity || 0)
        }));

        res.json(simplified);
    } catch (err) {
        console.error('Error al obtener inventarios disponibles:', err);
        res.status(500).json({ error: 'Error al obtener inventarios disponibles' });
    }
});

router.get('/availableByCategory', async (req, res) => {
    try {
        const response = await getListAsync();
        const inventories = (response && response.inventories) ? response.inventories : [];

        const simplified = [];
        inventories.forEach(inventory => {
            const id = inventory.idInventory;
            const category = inventory.category;
            const description = inventory.description;
            const inventoryObject = {
                id: id,
                category: category,
                description: description
            };
            simplified.push(inventoryObject);
        });

        res.json(simplified);
    } catch (err) {
        console.error('Error al obtener inventarios disponibles:', err);
        res.status(500).json({ error: 'Error al obtener inventarios disponibles' });
    }
});

// Rutas de Acciones (POST)
router.post('/', async (req, res) => {
    try {
        const { category, description, quantity } = req.body;
        const inventoryDto = {
            category,
            description,
            quantity: Number(quantity)
        };
        await createAsync(inventoryDto);
        res.redirect('/inventories');
    } catch (err) {
        console.error('Error al crear el inventario:', err);
        res.status(500).send('Error al crear el inventario.');
    }
});

router.post('/:id/update', async (req, res) => {
    try {
        const id = req.params.id;
        const { description, quantity } = req.body;
        const inventoryDto = {
            idInventory: Number(id),
            description,
            quantity: Number(quantity)
        };
        await updateAsync(inventoryDto);
        res.redirect('/inventories');
    } catch (err) {
        console.error('Error al actualizar el inventario:', err);
        res.status(500).send('Error al actualizar el inventario.');
    }
});

router.post('/:id/delete', async (req, res) => {
    try {
        const id = req.params.id;
        await deleteAsync(id);
        res.redirect('/inventories');
    } catch (err) {
        console.error('Error al eliminar el inventario:', err);
        res.status(500).send('Error al eliminar el inventario.');
    }
});

module.exports = router;