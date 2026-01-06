package com.techforb.apiportalrecruiting.core.services.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.dtos.users.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.LoginResponseDTO;


public interface EmailAuthenticationStrategy {

    void register(EmailLoginRequestDTO request) throws FirebaseAuthException;
    
    LoginResponseDTO login(EmailLoginRequestDTO request) throws FirebaseAuthException;

    String getStrategyType();
} 