package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LinkedInCallbackRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.AuthenticationContextService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.EmailAuthenticationStrategy;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.GoogleAuthenticationStrategy;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.LinkedInAuthenticationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthenticationContextImpl implements AuthenticationContextService {
    
    private final EmailAuthenticationStrategy emailStrategy;
    private final GoogleAuthenticationStrategy googleStrategy;
    private final LinkedInAuthenticationStrategy linkedInStrategy;
    private final LocalizedMessageService localizedMessageService;

    @Override
    public void register(String strategyType, Object request) throws FirebaseAuthException {
        switch (strategyType.toUpperCase()) {
            case "EMAIL":
                if (request instanceof EmailLoginRequestDTO) {
                    emailStrategy.register((EmailLoginRequestDTO) request);
                } else {
                    throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.email.invalid_request"));
                }
                break;
            case "GOOGLE":
                if (request instanceof GoogleLoginRequestDTO) {
                    googleStrategy.register((GoogleLoginRequestDTO) request);
                } else {
                    throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.google.invalid_request"));
                }
                break;
            case "LINKEDIN":
                if (request instanceof LinkedInCallbackRequestDTO) {
                    linkedInStrategy.register((LinkedInCallbackRequestDTO) request);
                } else {
                    throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.linkedin.invalid_request"));
                }
                break;
            default:
                throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.unsupported", strategyType));
        }
    }
    
    @Override
    public LoginResponseDTO login(String strategyType, Object request) throws FirebaseAuthException {
        switch (strategyType.toUpperCase()) {
            case "EMAIL":
                if (request instanceof EmailLoginRequestDTO) {
                    return emailStrategy.login((EmailLoginRequestDTO) request);
                } else {
                    throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.email.invalid_request"));
                }
            case "GOOGLE":
                if (request instanceof GoogleLoginRequestDTO) {
                    return googleStrategy.login((GoogleLoginRequestDTO) request);
                } else {
                    throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.google.invalid_request"));
                }
            case "LINKEDIN":
                if (request instanceof LinkedInCallbackRequestDTO) {
                    return linkedInStrategy.login((LinkedInCallbackRequestDTO) request);
                } else {
                    throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.linkedin.invalid_request"));
                }
            default:
                throw new IllegalArgumentException(localizedMessageService.getMessage("auth.strategy.unsupported", strategyType));
        }
    }
} 