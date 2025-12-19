package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;

public interface GoogleAuthenticationStrategy {

    void register(GoogleLoginRequestDTO request) throws FirebaseAuthException;

    LoginResponseDTO login(GoogleLoginRequestDTO request) throws FirebaseAuthException;

    String getStrategyType();
} 