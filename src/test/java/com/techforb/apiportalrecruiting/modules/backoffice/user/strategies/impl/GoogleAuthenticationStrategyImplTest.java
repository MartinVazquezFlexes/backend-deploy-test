package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthService;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.UserLoginResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class GoogleAuthenticationStrategyImplTest {

    @InjectMocks
    private GoogleAuthenticationStrategyImpl googleAuthenticationStrategy;
    @Mock
    private UserService userService;

    @Mock
    private ModelMapperUtils modelMapperUtils;

    @Mock
    private LocalizedMessageService localizedMessageService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JwtService jwtService;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @Mock
    private AuthenticationManager authenticationManager;

    private GoogleLoginRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new GoogleLoginRequestDTO();
        requestDTO.setAccessToken("google-access-token");


        // ✅ Inyecta manualmente el API_KEY antes del @PostConstruct
        ReflectionTestUtils.setField(
                googleAuthenticationStrategy,
                "apiKey",
                "test-api-key"
        );

        // ✅ Llama manualmente al @PostConstruct
        googleAuthenticationStrategy.init();
    }

    @Test
    void register_Throws_Exception() {
        UnsupportedOperationException ex= Assertions.assertThrows(UnsupportedOperationException.class,
                ()->googleAuthenticationStrategy.register(requestDTO));
        Assertions.assertEquals("El registro con Google se maneja automáticamente durante el login",ex.getMessage());
    }

    @Test
    void login_Success() throws FirebaseAuthException {
        Map<String, Object> firebaseResponse = new HashMap<>();
        firebaseResponse.put("idToken", "firebase-token");
        firebaseResponse.put("email", "test@email.com");
        firebaseResponse.put("isNewUser", true);

        CustomUserDetails userDetails = Mockito.mock(CustomUserDetails.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        UserEntity userEntity = new UserEntity();
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        Mockito.when(
                restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Map.class))
        ).thenReturn(firebaseResponse);

        Mockito.doNothing().when(firebaseAuthService).verifyToken("firebase-token");

        Mockito.when(userService.findByEmail("test@email.com"))
                .thenReturn(Optional.empty());

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(authentication);

        Mockito.when(authentication.getPrincipal())
                .thenReturn(userDetails);

        Mockito.when(userDetails.getUserEntity())
                .thenReturn(userEntity);

        Mockito.when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        Mockito.when(modelMapperUtils.map(userEntity, UserLoginResponseDTO.class))
                .thenReturn(userLoginResponseDTO);

        LoginResponseDTO response = googleAuthenticationStrategy.login(requestDTO);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getJwt());
        Assertions.assertEquals("jwt-token", response.getJwt().token());

        Mockito.verify(firebaseAuthService).verifyToken("firebase-token");
        Mockito.verify(authenticationManager).authenticate(Mockito.any());
        Mockito.verify(userService).createUser("test@email.com", "APPLICANT", null, null);
    }
    @Test
    void login_Success_NotNewUser_ButNotInDatabase() throws FirebaseAuthException {
        Map<String, Object> firebaseResponse = new HashMap<>();
        firebaseResponse.put("idToken", "firebase-token");
        firebaseResponse.put("email", "test@email.com");

        CustomUserDetails userDetails = Mockito.mock(CustomUserDetails.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        UserEntity userEntity = new UserEntity();
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        Mockito.when(
                restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Map.class))
        ).thenReturn(firebaseResponse);

        Mockito.doNothing().when(firebaseAuthService).verifyToken("firebase-token");

        Mockito.when(userService.findByEmail("test@email.com"))
                .thenReturn(Optional.empty());

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(authentication);

        Mockito.when(authentication.getPrincipal())
                .thenReturn(userDetails);

        Mockito.when(userDetails.getUserEntity())
                .thenReturn(userEntity);

        Mockito.when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        Mockito.when(modelMapperUtils.map(userEntity, UserLoginResponseDTO.class))
                .thenReturn(userLoginResponseDTO);

        LoginResponseDTO response = googleAuthenticationStrategy.login(requestDTO);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getJwt());
        Assertions.assertEquals("jwt-token", response.getJwt().token());

        Mockito.verify(firebaseAuthService).verifyToken("firebase-token");
        Mockito.verify(authenticationManager).authenticate(Mockito.any());

        Mockito.verify(userService).createUser("test@email.com", "APPLICANT", null, null);
    }

    @Test
    void login_Invalid_Token(){
        Map<String, Object> firebaseResponse = new HashMap<>();
        firebaseResponse.put("email", "test@email.com");

        Mockito.when(
                restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Map.class))
        ).thenReturn(firebaseResponse);

        Mockito.when(
                localizedMessageService.getMessage("firebase.invalid_id_token")
        ).thenReturn("firebase.invalid_id_token");

        AuthenticationServiceException ex = Assertions.assertThrows(
                AuthenticationServiceException.class,
                () -> googleAuthenticationStrategy.login(requestDTO)
        );

        Assertions.assertEquals("firebase.invalid_id_token", ex.getMessage());
    }
    @Test
    void login_Firebase_Error(){
        Mockito.when(
                restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Map.class))
        ).thenThrow(new RestClientException("firebase error"));

        AuthenticationServiceException ex = Assertions.assertThrows(
                AuthenticationServiceException.class,
                () -> googleAuthenticationStrategy.login(requestDTO)
        );

        Assertions.assertTrue(ex.getMessage().contains("Auth with firebase error"));
    }
    @Test
    void getStrategyType() {
        Assertions.assertEquals("GOOGLE",googleAuthenticationStrategy.getStrategyType());
    }
}