const jwt = require("jsonwebtoken"); //para validar token entrante

const authenticateToken = (req, res, next) => {
  const token = req.cookies.token;

  if (token == null) return res.render("index", {}); //si no hay token lo mandamos al login

  jwt.verify(token, process.env.JWT_PRIVATE_KEY, (err, payload) => {
    if (err) {
        return res.render("index", {});//si el token no es valido lo mandamos al login
    };
    req.user = {username: payload.sub, id: payload.id, roles: payload.roles};
    next();
  });
};

module.exports = authenticateToken;