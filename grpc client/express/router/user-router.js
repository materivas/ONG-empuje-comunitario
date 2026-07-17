const express = require('express');
const jwtAuth = require("../auth/jwt-auth");
const userRouter = express.Router();
const grpc = require('@grpc/grpc-js');
const grpcUserService = require('../../clients/userClient');

//-------------------------- rutas de vistas -------------------------------
//estoy repitiendo codigo en las vistas (verificar rol presidente) pero es algo temporal, despues refactorizo
userRouter.get("/", jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE")) {
        res.render("error/error-403");
        return;
    }
    res.render("user/user");
});

userRouter.get("/create", jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE")) {
        res.render("error/error-403");
        return;
    }
    res.render("user/createUser");
});

userRouter.get("/modify/:username", jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE")) {
        res.render("error/error-403");
        return;
    }
    res.render("user/modifyUser", {username: req.params.username});
});

//-------------------------- rutas de la api -------------------------------
userRouter.post("/login", (req, res) => {
    const username = req.body.username;
    const password = req.body.password;
    const requestData = {username: username, password: password};

    grpcUserService.Login(requestData, (err, response) => {
        onServerResponse(res, err, response, (response) => {
            res.cookie('token', response.token, {
                httpOnly: true,
                secure: process.env.NODE_ENV === 'production',
                sameSite: 'strict',
                maxAge: 1800000 //tiempo de vida de esta cookie. le ponemos la misma del token
            });
            res.cookie('user', response.userWithRolesDTO, {
                httpOnly: true,
                secure: process.env.NODE_ENV === 'production',
                sameSite: 'strict',
                maxAge: 1800000 //tiempo de vida de esta cookie. le ponemos la misma del token
            });
            res.send({user: response.userWithRolesDTO, token: response.token});
        }
        )
    });
});

userRouter.post("/logout", (req, res) => {
    res.clearCookie('token', {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'strict',
        path: '/'
    });
    res.clearCookie('user', {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'strict',
        path: '/'
    });
    res.send();
});

userRouter.post("/create", (req, res) => {
    const metadata = buildMetadataWithToken(req);
    
    const requestData = {
        username: req.body.username,
        name: req.body.name,
        surname: req.body.surname,
        phoneNumber: req.body.phoneNumber,
        email: req.body.email,
        isActive: req.body.isActive,
        roles: req.body.roles
    };

    grpcUserService.CreateUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => 
            res.send({message: response.message, succeeded: response.succeeded})
        )
    });
});

userRouter.post("/modify", (req, res) => {
    const id = req.body.id;
    const userWithRolesDTO = req.body.userWithRolesDTO;
    const metadata = buildMetadataWithToken(req);

    const requestData = {id: id, userWithRolesDTO: userWithRolesDTO};

    grpcUserService.ModifyUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => 
            res.send({message: response.message, succeeded: response.succeeded})
        )
    });
});

userRouter.post("/delete", (req, res) => {
    const id = req.body.id;
    const metadata = buildMetadataWithToken(req);

    const requestData = {id: id};

    grpcUserService.DeleteUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) =>
            res.send({message: response.message, succeeded: response.succeeded})
        )
    });
});

userRouter.get("/list", (req, res) => {
    const metadata = buildMetadataWithToken(req);

    const requestData = {};

    grpcUserService.GetUserList(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => res.send({users: response.users}))
    });
});

userRouter.get("/active-list", (req, res) => {
    const metadata = buildMetadataWithToken(req);

    const requestData = {};

    grpcUserService.GetActiveUsersList(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => res.send({users: response.users}))
    });
});

userRouter.get("/:username", (req, res) => {
    const username = req.params.username;
    const metadata = buildMetadataWithToken(req);

    const requestData = {username: username};

    grpcUserService.GetUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response,
            (response) => res.send({id: response.id, userWithRolesDTO: response.userWithRolesDTO})
        )
    });
});

function onServerResponse(res, err, response, sendResponse) {
    if(err) {
        res.send(err);
        return;
    }
    sendResponse(response);
}

function buildMetadataWithToken(req) {
    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null) {
        metadata.add('Authorization', 'Bearer '+token);
    }
    return metadata;
}

module.exports = userRouter;