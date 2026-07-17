const express = require('express');
const jwtAuth = require("../auth/jwt-auth");
const router = express.Router();
const soapClient = require('../../soap/soap-client');

//-------------------------- rutas SOAP -------------------------------

router.get('/list-presidents', async (req, res) => {
    const ids = req.query.ids || '';
    const idsArray = ids
        .split(',')
        .map(id => id.trim())
        .filter(id => id !== '');

    try{
        const data =  await soapClient.list_presidents(idsArray);
        const presidentsList = data.list_presidentsResult?.PresidentType || [];
        res.json({ presidentsList });
    } catch (error){
        console.error('Error al obtener la lista de presidentes: ', error);
        res.status(500).json({error: 'Error al obtener la lista de presidentes'});
    }
})

router.get('/list-associations', async (req, res) => {
    const ids = req.query.ids || '';
    const idsArray = ids
        .split(',')
        .map(id => id.trim())
        .filter(id => id !== '');

    try{
        const data =  await soapClient.list_associations(idsArray);
        const associationsList = data.list_associationsResult?.OrganizationType || [];
        res.json({ associationsList });
    } catch (error){
        console.error('Error al obtener la lista de organizaciones: ', error);
        res.status(500).json({error: 'Error al obtener la lista de organizaciones'});
    }
})

//-------------------------- rutas de vistas -------------------------------

router.get('/', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE")){
        res.render("error/error-403");
        return;
    }
    res.render('queries/queries');
})

module.exports = router;