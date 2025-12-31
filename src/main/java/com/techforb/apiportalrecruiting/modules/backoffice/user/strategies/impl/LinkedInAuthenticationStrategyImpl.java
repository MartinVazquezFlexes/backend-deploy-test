package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.exceptions.LinkedInAuthenticationException;
import com.techforb.apiportalrecruiting.core.security.jwt.Jwt;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.security.linkedin.LinkedInService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LinkedInCallbackRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.UserLoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.LinkedInAuthenticationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkedInAuthenticationStrategyImpl implements LinkedInAuthenticationStrategy {

    private final LinkedInService linkedInService;
    private final UserService userService;
    private final JwtService jwtService;
    private final ModelMapperUtils modelMapperUtils;
    private final LocalizedMessageService localizedMessageService;

    @Override
    public LoginResponseDTO login(LinkedInCallbackRequestDTO request) throws FirebaseAuthException {
        LinkedInCallbackRequestDTO callbackRequest = request;

        try {
            String accessToken = linkedInService.getAccessToken(callbackRequest.getCode());
            
            Map<String, Object> profile = linkedInService.getUserProfile(accessToken);
            String email = linkedInService.getUserEmail(accessToken);
            
            UserEntity user = userService.findByEmail(email).orElse(null);
            
            if (user == null) {
                user = userService.createUser(email, "DEFAULT", null, profile);
            }
            
            CustomUserDetails userDetails = new CustomUserDetails(user);
            
            Jwt jwt = new Jwt(jwtService.generateToken(userDetails));
            
            UserLoginResponseDTO userResponse = modelMapperUtils.map(user, UserLoginResponseDTO.class);
            
            return new LoginResponseDTO(userResponse, jwt);

        } catch (Exception e) {
            log.error("Error en LinkedIn authentication: {}", e.getMessage(), e);
            throw new LinkedInAuthenticationException(
                    localizedMessageService.getMessage("auth.linkedin.error", e.getMessage()),
                    e
            );
        }
    }

    @Override
    public void register(LinkedInCallbackRequestDTO request) throws FirebaseAuthException {
        throw new UnsupportedOperationException(localizedMessageService.getMessage("auth.linkedin.registration_not_supported"));
    }

    @Override
    public String getStrategyType() {
        return "LINKEDIN";
    }
} 