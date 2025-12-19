package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies;
import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;

public interface AuthenticationContextService {

    void register(String strategyType, Object request) throws FirebaseAuthException;

    LoginResponseDTO login(String strategyType, Object request) throws FirebaseAuthException;
} 