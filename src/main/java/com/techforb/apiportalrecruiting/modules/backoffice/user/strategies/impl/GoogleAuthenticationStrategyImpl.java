package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthService;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthenticationToken;
import com.techforb.apiportalrecruiting.core.security.jwt.Jwt;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.UserLoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.GoogleAuthenticationStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleAuthenticationStrategyImpl implements GoogleAuthenticationStrategy {

    @Value("${API_KEY}")
    private String apiKey;
    private String firebaseAuthUrlWithProvider;
    
    private final UserService userService;
    private final ModelMapperUtils modelMapperUtils;
    private final LocalizedMessageService localizedMessageService;
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final FirebaseAuthService firebaseAuthService;
    private final AuthenticationManager authenticationManager;

    @PostConstruct
    public void init() {
        this.firebaseAuthUrlWithProvider =
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + apiKey;
    }

    
    @Override
    public void register(GoogleLoginRequestDTO request) throws FirebaseAuthException {
        throw new UnsupportedOperationException("El registro con Google se maneja automáticamente durante el login");
    }
    
    @Override
    public LoginResponseDTO login(GoogleLoginRequestDTO request) throws FirebaseAuthException {
        GoogleLoginRequestDTO googleRequest = request;
        String accessToken = googleRequest.getAccessToken();
        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("postBody", "access_token=" + accessToken + "&providerId=google.com");
        requestMap.put("requestUri", "http://localhost");
        requestMap.put("returnIdpCredential", true);
        requestMap.put("returnSecureToken", true);
        
        Map<String, Object> response;
        try {
            response = callFirebaseAuth(firebaseAuthUrlWithProvider, requestMap);
        } catch (RestClientException e) {
            throw new AuthenticationServiceException("Auth with firebase error: " + e.getMessage(), e);
        }
        
        if (response == null || !response.containsKey("idToken")) {
            throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.invalid_id_token"));
        }
        
        String idToken = (String) response.get("idToken");
        firebaseAuthService.verifyToken(idToken);
        
        String email = (String) response.get("email");
        
        // Si es un usuario nuevo o no existe en la BD, lo agregamos
        if (response.containsKey("isNewUser") && userService.findByEmail(email).isEmpty()) {
            userService.createUser(email, "APPLICANT", null, null);
        }
        
        if (!response.containsKey("isNewUser") && userService.findByEmail(email).isEmpty()) {
            userService.createUser(email, "APPLICANT", null, null);
        }
        
        Authentication auth = authenticationManager.authenticate(
                new FirebaseAuthenticationToken(idToken)
        );
        
        CustomUserDetails userAuthenticated = (CustomUserDetails) auth.getPrincipal();

        Jwt jwt = new Jwt(
                jwtService.generateToken(userAuthenticated)
        );
        
        UserLoginResponseDTO userResponse = modelMapperUtils.map(userAuthenticated.getUserEntity(), UserLoginResponseDTO.class);
        
        return new LoginResponseDTO(userResponse, jwt);
    }
    
    @Override
    public String getStrategyType() {
        return "GOOGLE";
    }
    

    
    private Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
        return restTemplate.postForObject(url, request, Map.class);
    }
} 