package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;


public interface AuthenticationStrategy {
    
    void register(Object request) throws FirebaseAuthException;

    LoginResponseDTO login(Object request) throws FirebaseAuthException;
    
    String getStrategyType();
} 