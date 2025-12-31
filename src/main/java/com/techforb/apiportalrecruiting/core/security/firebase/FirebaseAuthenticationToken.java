package com.techforb.apiportalrecruiting.core.security.firebase;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Objects;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final String idToken;

    public FirebaseAuthenticationToken(String idToken) {
        super(null);
        this.idToken = idToken;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return idToken;
    }

    @Override
    public Object getPrincipal() {
        return null; // Se define luego en el AuthenticationProvider
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FirebaseAuthenticationToken)) return false;
        if (!super.equals(o)) return false;

        FirebaseAuthenticationToken that = (FirebaseAuthenticationToken) o;
        return Objects.equals(idToken, that.idToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idToken);
    }
}
