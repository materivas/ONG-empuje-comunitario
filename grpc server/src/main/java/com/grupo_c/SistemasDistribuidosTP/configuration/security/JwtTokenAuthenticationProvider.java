package com.grupo_c.SistemasDistribuidosTP.configuration.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtTokenAuthenticationProvider {
    private final JwtUtils jwtUtils;
    public JwtTokenAuthenticationProvider(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public JwtAuthentication authenticate(JwtAuthentication jwtAuthentication) throws AuthenticationException, JWTVerificationException {
        String token = jwtAuthentication.getToken().replaceFirst("Bearer ", "");

        if(token.isBlank())
            throw new BadCredentialsException("JWT can not be empty.");

        //extraigo informacion del token
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String username = jwtUtils.extractUsernameFromToken(decodedJWT);
        String rolesSeparatedByComma = jwtUtils.extractClaimFromToken(decodedJWT, "roles").asString();
        Set<String> roles = new HashSet<>();
        for(String currentRole : rolesSeparatedByComma.split(","))
            roles.add(currentRole.trim());

        return new JwtAuthentication(token, username, roles);
    }
}
