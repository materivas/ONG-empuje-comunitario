package com.grupo_c.SistemasDistribuidosTP.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.user.generator}")
    private String userGenerator;
    @Value("${spring.security.jwt.private.key}")
    private String privateKey;

    public String createToken(User userEntity, Long expirationTimeInMillis) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);

            return JWT.create()
                    .withIssuer(userGenerator) //usuario que genera el token (nuestro backend)
                    .withSubject(userEntity.getUsername()) //usuario al que le genero el token
                    .withClaim("roles", userEntity.getRolesSeparatedByCommaAsString()) //incluyo los roles del usuario en el token
                    .withClaim("id", userEntity.getId()) //se incluye el id del usuario en el token
                    .withIssuedAt(new Date()) //fecha de emision del token (ahora mismo)
                    .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInMillis)) //fecha de expiracion del token. En media hora
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(new Date(System.currentTimeMillis())) //aca indico que el token se puede usar ahora mismo
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new JWTCreationException("Couldn't create JWT.", exception);
        }
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(userGenerator)
                .build();
        return verifier.verify(token);
    }

    public String extractUsernameFromToken(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim extractClaimFromToken(DecodedJWT decodedJWT, String claim) {
        return decodedJWT.getClaim(claim);
    }

    public Map<String, Claim> extractAllClaimsFromToken(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
}
