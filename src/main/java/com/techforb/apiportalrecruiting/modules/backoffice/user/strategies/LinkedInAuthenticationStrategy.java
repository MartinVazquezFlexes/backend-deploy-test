package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LinkedInCallbackRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;


public interface LinkedInAuthenticationStrategy {

    void register(LinkedInCallbackRequestDTO request) throws FirebaseAuthException;

    LoginResponseDTO login(LinkedInCallbackRequestDTO request) throws FirebaseAuthException;

    String getStrategyType();
} 