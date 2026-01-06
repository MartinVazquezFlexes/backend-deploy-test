package com.techforb.apiportalrecruiting.core.services.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.dtos.users.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.LoginResponseDTO;

public interface GoogleAuthenticationStrategy {

    void register(GoogleLoginRequestDTO request) throws FirebaseAuthException;

    LoginResponseDTO login(GoogleLoginRequestDTO request) throws FirebaseAuthException;

    String getStrategyType();
} 