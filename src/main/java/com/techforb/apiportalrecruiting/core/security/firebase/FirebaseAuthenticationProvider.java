package com.techforb.apiportalrecruiting.core.security.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final LocalizedMessageService localizedMessageService;

    public FirebaseAuthenticationProvider(UserDetailsService userDetailsService, LocalizedMessageService localizedMessageService) {
        this.userDetailsService = userDetailsService;
        this.localizedMessageService = localizedMessageService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String idToken = (String) authentication.getCredentials();

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        } catch (FirebaseAuthException e) {
            throw new RuntimeException(localizedMessageService.getMessage("firebase.invalid_id_token"), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

