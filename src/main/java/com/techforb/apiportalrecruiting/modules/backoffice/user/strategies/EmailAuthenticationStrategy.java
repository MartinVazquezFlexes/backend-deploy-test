package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;


public interface EmailAuthenticationStrategy {

    void register(EmailLoginRequestDTO request) throws FirebaseAuthException;
    
    LoginResponseDTO login(EmailLoginRequestDTO request) throws FirebaseAuthException;

    String getStrategyType();
} 