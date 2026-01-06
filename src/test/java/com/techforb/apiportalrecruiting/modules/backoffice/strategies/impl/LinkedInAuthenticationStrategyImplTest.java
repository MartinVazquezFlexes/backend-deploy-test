package com.techforb.apiportalrecruiting.modules.backoffice.strategies.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.security.jwt.Jwt;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.security.linkedin.LinkedInService;
import com.techforb.apiportalrecruiting.core.security.CustomUserDetails;
import com.techforb.apiportalrecruiting.core.services.UserService;
import com.techforb.apiportalrecruiting.core.services.strategies.impl.LinkedInAuthenticationStrategyImpl;
import com.techforb.apiportalrecruiting.core.dtos.users.LinkedInCallbackRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.LoginResponseDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.UserLoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkedInAuthenticationStrategyImplTest {
    @Mock
    private LinkedInService linkedinService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private LocalizedMessageService localizedMessageService;

    @Mock
    private ModelMapperUtils modelMapperUtils;

    @InjectMocks
    private LinkedInAuthenticationStrategyImpl linkedInAuthenticationStrategy;

    private LinkedInCallbackRequestDTO linkedInRequest;
    private LoginResponseDTO loginResponse;
    private String accessToken;
    private String code;
    private String state;
    private Map<String, Object> profile;
    private String email;
    private UserEntity user;
    private UserLoginResponseDTO userLoginResponseDTO;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        code = "test_code";
        state = "test_state";
        accessToken = "access_token";
        email = "john.doe@example.com";

        linkedInRequest = new LinkedInCallbackRequestDTO();
        linkedInRequest.setState(state);
        linkedInRequest.setCode(code);

        profile = new HashMap<>();
        profile.put("id", "linkedin_id");
        profile.put("firstName", "John");
        profile.put("lastName", "Doe");

        user = new UserEntity();
        user.setEmail(email);

        userLoginResponseDTO = new UserLoginResponseDTO();
        userLoginResponseDTO.setEmail(email);

        jwt = new Jwt("jwt_token");
    }

    @Test
    void login() throws FirebaseAuthException {
        when(linkedinService.getAccessToken(linkedInRequest.getCode())).thenReturn(accessToken);
        when(linkedinService.getUserProfile(accessToken)).thenReturn(profile);
        when(linkedinService.getUserEmail(accessToken)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("jwt_token");
        when(modelMapperUtils.map(user, UserLoginResponseDTO.class)).thenReturn(userLoginResponseDTO);

        loginResponse = linkedInAuthenticationStrategy.login(linkedInRequest);

        assertNotNull(loginResponse);
        assertEquals(userLoginResponseDTO, loginResponse.getUser());
        assertEquals(jwt.token(), loginResponse.getJwt().token());
        
        verify(linkedinService).getAccessToken(linkedInRequest.getCode());
        verify(linkedinService).getUserProfile(accessToken);
        verify(linkedinService).getUserEmail(accessToken);
        verify(userService).findByEmail(email);
        verify(jwtService).generateToken(any(CustomUserDetails.class));
        verify(modelMapperUtils).map(user, UserLoginResponseDTO.class);
        verifyNoMoreInteractions(linkedinService, userService, jwtService, modelMapperUtils);
    }

    @Test
    void login_WhenUserDoesNotExist_CreatesNewUser() throws FirebaseAuthException {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setId(2L);

        when(linkedinService.getAccessToken(linkedInRequest.getCode())).thenReturn(accessToken);
        when(linkedinService.getUserProfile(accessToken)).thenReturn(profile);
        when(linkedinService.getUserEmail(accessToken)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.empty());
        when(userService.createUser(email, "APPLICANT", null, profile)).thenReturn(newUser);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("jwt_token");
        when(modelMapperUtils.map(newUser, UserLoginResponseDTO.class)).thenReturn(userLoginResponseDTO);

        loginResponse = linkedInAuthenticationStrategy.login(linkedInRequest);

        assertNotNull(loginResponse);
        assertEquals(userLoginResponseDTO, loginResponse.getUser());
        assertEquals(jwt.token(), loginResponse.getJwt().token());
        
        verify(linkedinService).getAccessToken(linkedInRequest.getCode());
        verify(linkedinService).getUserProfile(accessToken);
        verify(linkedinService).getUserEmail(accessToken);
        verify(userService).findByEmail(email);
        verify(userService).createUser(email, "APPLICANT", null, profile);
        verify(jwtService).generateToken(any(CustomUserDetails.class));
        verify(modelMapperUtils).map(newUser, UserLoginResponseDTO.class);
        verifyNoMoreInteractions(linkedinService, userService, jwtService, modelMapperUtils);
    }

    @Test
    void login_WhenLinkedInServiceThrowsException_PropagatesRuntimeException() {
        String errorMessage = "LinkedIn service error";
        when(linkedinService.getAccessToken(linkedInRequest.getCode())).thenThrow(new RuntimeException(errorMessage));
        when(localizedMessageService.getMessage("auth.linkedin.error", errorMessage))
                .thenReturn("LinkedIn authentication error: " + errorMessage);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> linkedInAuthenticationStrategy.login(linkedInRequest)
        );

        assertEquals("LinkedIn authentication error: " + errorMessage, exception.getMessage());
        verify(linkedinService).getAccessToken(linkedInRequest.getCode());
        verify(localizedMessageService).getMessage("auth.linkedin.error", errorMessage);
        verifyNoMoreInteractions(linkedinService, userService, jwtService, modelMapperUtils);
    }

    @Test
    void register() {
        when(localizedMessageService.getMessage("auth.linkedin.registration_not_supported"))
                .thenReturn("LinkedIn no requiere registro separado");

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> linkedInAuthenticationStrategy.register(linkedInRequest)
        );

        assertEquals("LinkedIn no requiere registro separado", exception.getMessage());
    }

    @Test
    void getStrategyType() {
        String strategyType = linkedInAuthenticationStrategy.getStrategyType();

        assertEquals("LINKEDIN", strategyType);
    }
}