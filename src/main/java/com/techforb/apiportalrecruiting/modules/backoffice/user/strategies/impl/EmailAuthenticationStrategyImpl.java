package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthService;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthenticationToken;
import com.techforb.apiportalrecruiting.core.security.jwt.Jwt;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.UserLoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.EmailAuthenticationStrategy;
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
public class EmailAuthenticationStrategyImpl implements EmailAuthenticationStrategy {

    @Value("${API_KEY}")
    private String apiKey;
    private String firebaseAuthUrlWithPassword;
    private final UserRepository userRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapperUtils modelMapperUtils;
    private final LocalizedMessageService localizedMessageService;
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        this.firebaseAuthUrlWithPassword =
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;
    }
    
    @Override
    public void register(EmailLoginRequestDTO request) throws FirebaseAuthException {
        EmailLoginRequestDTO newUser = request;
        String email = newUser.getEmail();
        String password = newUser.getPassword();
        
        firebaseAuthService.registerUser(email, password);
        
        if (userRepository.findByEmail(email).isEmpty()) {
            this.userService.addUserToDb(email, "APPLICANT", password);
        }
    }
    
    @Override
    public LoginResponseDTO login(EmailLoginRequestDTO request) throws FirebaseAuthException {
        EmailLoginRequestDTO loginRequestDTO = request;
        String email = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();
        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("email", email);
        requestMap.put("password", password);
        requestMap.put("returnSecureToken", true);
        
        Map<String, Object> response;
        try {
            response = callFirebaseAuth(firebaseAuthUrlWithPassword, requestMap);
        } catch (RestClientException e) {
            throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.auth.error", e.getMessage()), e);
        }
        
        if (response == null || !response.containsKey("idToken")) {
            throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.invalid_id_token"));
        }
        
        String idToken = (String) response.get("idToken");
        firebaseAuthService.verifyToken(idToken);
        
        if (userRepository.findByEmail(email).isEmpty()) {
            this.userService.createUser(email,"APPLICANT", password,null);
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
        return "EMAIL";
    }
    

    
    private Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
        return restTemplate.postForObject(url, request, Map.class);
    }
} 