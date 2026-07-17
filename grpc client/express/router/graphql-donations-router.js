const express = require('express');
const graphQLDonationsRouter = express.Router();
const jwtAuth = require('../auth/jwt-auth');

//views
graphQLDonationsRouter.get('/our-donations-report', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render('donations/ourDonationsReport', { userId: req.user.id });
});

graphQLDonationsRouter.get('/received-donations-report', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render('donations/receivedDonationsReport', { userId: req.user.id });
});

graphQLDonationsRouter.get('/my-filters', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render('donations/myFilters', { userId: req.user.id });
});

graphQLDonationsRouter.get('/update-filter/:idFilter', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render('donations/updateFilter', { idFilter: req.params.idFilter, idUser: req.user.id });
});

module.exports = graphQLDonationsRouter;