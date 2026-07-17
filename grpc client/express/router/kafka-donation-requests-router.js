const express = require('express');
const jwtAuth = require("../auth/jwt-auth");
const donationRequestsRouter = express.Router();
const donationRequestConsumer = require("../../kafka/consumers/donationRequests.js");
const donationRequestsDeletedConsumer = require("../../kafka/consumers/donationRequestsDeleted.js");

//views
donationRequestsRouter.get("/allRequests", jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render("donations/donationRequests");
});

donationRequestsRouter.get("/ourRequests", jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render("donations/ourDonationRequests");
});

donationRequestsRouter.get("/create", jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render("donations/createDonationRequest");
});

//api
donationRequestsRouter.get("/all", jwtAuth, (req, res) => {
    const allDonationRequests = donationRequestConsumer.getAllDonationRequestsExceptOurs();
    res.send({ allDonationRequests });
});
donationRequestsRouter.get("/ours", jwtAuth, (req, res) => {
    const ourDonationRequests = donationRequestConsumer.getOurDonationRequests();
    res.send({ ourDonationRequests });
});
donationRequestsRouter.post("/create", jwtAuth, (req, res) => {
    let maxRequestId = 1;
    const donationRequests = donationRequestConsumer.getAllDonationRequestsAndDeletedOnes();
    for(let i = 0 ; i < donationRequests.length ; i++) {
        if(donationRequests[i].request_id > maxRequestId) {
            maxRequestId = donationRequests[i].request_id;
        }
    }
    const organizationId = 1;
    const requestId = maxRequestId + 1;
    const donations = req.body.donations;
    const donationRequest = {
        organization_id : organizationId,
        request_id : requestId,
        donations: donations
    };
    try {
        donationRequestConsumer.publish(donationRequest);
        res.send({ succeeded: true, message: "Donación publicada correctamente." });
    } catch(err) {
        res.send({ succeeded: false, message: "No se pudo publicar la solicitud de donación. Vuelva a intentarlo, por favor." });
    }
});
donationRequestsRouter.post("/delete", jwtAuth, (req, res) => {
    const organizationId = req.body.organizationId;
    const requestId = req.body.requestId;
    const deletedAt = req.body.deleted_at;
    const message = {
        organization_id : organizationId,
        request_id : requestId,
        deleted_at : deletedAt
    };
    try {
        donationRequestsDeletedConsumer.publish(message);
        res.send({ succeeded: true, message: "Donación eliminada correctamente." });
    } catch(err) {
        res.send({ succeeded: false, message: "No se pudo eliminar la solicitud de donación. Vuelva a intentarlo, por favor." });
    }
});

module.exports = donationRequestsRouter;