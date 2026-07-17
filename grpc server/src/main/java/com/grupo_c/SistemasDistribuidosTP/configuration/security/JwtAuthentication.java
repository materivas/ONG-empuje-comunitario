package com.grupo_c.SistemasDistribuidosTP.configuration.security;

import lombok.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter @Setter
@ToString
public class JwtAuthentication {
    private final String token;
    private final String username;
    private Set<String> roles;
    private boolean authenticated;

    // constructor para token aun no autenticado
    public JwtAuthentication(String token) {
        this.token = token;
        this.username = null;
        this.roles = null;
        this.authenticated = false;
    }

    // constructor para token autenticado
    public JwtAuthentication(
            String token,
            String username,
            Set<String> roles
    ) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.authenticated = true;
    }
}
