package com.techforb.apiportalrecruiting.core.services.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.dtos.users.LoginResponseDTO;


public interface AuthenticationStrategy {
    
    void register(Object request) throws FirebaseAuthException;

    LoginResponseDTO login(Object request) throws FirebaseAuthException;
    
    String getStrategyType();
} 