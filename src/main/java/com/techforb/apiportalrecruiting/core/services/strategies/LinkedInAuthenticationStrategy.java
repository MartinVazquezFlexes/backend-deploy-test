package com.techforb.apiportalrecruiting.core.services.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.dtos.users.LinkedInCallbackRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.LoginResponseDTO;


public interface LinkedInAuthenticationStrategy {

    void register(LinkedInCallbackRequestDTO request) throws FirebaseAuthException;

    LoginResponseDTO login(LinkedInCallbackRequestDTO request) throws FirebaseAuthException;

    String getStrategyType();
} 