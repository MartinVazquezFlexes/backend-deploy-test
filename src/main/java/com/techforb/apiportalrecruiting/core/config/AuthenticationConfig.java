package com.techforb.apiportalrecruiting.core.config;

import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {
    private final UserDetailsService userDetailsService;
    private final LocalizedMessageService localizedMessageService;

    @Bean
    FirebaseAuthenticationProvider firebaseAuthenticationProvider(){
        return new FirebaseAuthenticationProvider(userDetailsService, localizedMessageService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws UnauthorizedActionException {
        return new ProviderManager(firebaseAuthenticationProvider());
    }
}
