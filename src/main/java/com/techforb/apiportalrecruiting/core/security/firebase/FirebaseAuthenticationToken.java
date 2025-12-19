package com.techforb.apiportalrecruiting.core.security.firebase;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final String idToken;

    public FirebaseAuthenticationToken(String idToken) {
        super(null);
        this.idToken = idToken;
        setAuthenticated(false); // Inicialmente no autenticado
    }

    public String getCredentials() {
        return idToken;
    }

    public Object getPrincipal() {
        return null; // Esto al principio no lo conocemos, se determina después por el provider
    }
}

