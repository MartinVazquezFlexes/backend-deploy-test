package com.techforb.apiportalrecruiting.core.services.strategies;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.dtos.users.LoginResponseDTO;

public interface AuthenticationContextService {

    void register(String strategyType, Object request) throws FirebaseAuthException;

    LoginResponseDTO login(String strategyType, Object request) throws FirebaseAuthException;
} 