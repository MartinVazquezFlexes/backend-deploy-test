package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthService;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EmailAuthenticationStrategyImplTest {

    @InjectMocks
    private EmailAuthenticationStrategyImpl emailAuthenticationStrategy;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ModelMapperUtils modelMapperUtils;

    @Mock
    private LocalizedMessageService localizedMessageService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    private EmailLoginRequestDTO requestDTO;

    @BeforeEach
    void setUp(){
        requestDTO = new EmailLoginRequestDTO();
        requestDTO.setEmail("test@email.com");
        requestDTO.setPassword("123456");
    }

    @Test
    void register_UserNotExist_AddToDB() throws FirebaseAuthException {
        Mockito.when(firebaseAuthService.registerUser(requestDTO.getEmail(),requestDTO.getPassword()))
                .thenReturn("firebaseUid");
        Mockito.when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.empty());
        emailAuthenticationStrategy.register(requestDTO);
        Mockito.verify(firebaseAuthService).registerUser(requestDTO.getEmail(),requestDTO.getPassword());
        Mockito.verify(userService).addUserToDb(requestDTO.getEmail(),"DEFAULT",requestDTO.getPassword());
    }

    @Test
    void register_UserExist() throws FirebaseAuthException{
        Mockito.when(firebaseAuthService.registerUser(requestDTO.getEmail(),requestDTO.getPassword()))
                .thenReturn("firebaseUid");
        Mockito.when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(new UserEntity()));
        emailAuthenticationStrategy.register(requestDTO);
        Mockito.verify(userService,Mockito.never()).addUserToDb(Mockito.any(),Mockito.any(),Mockito.any());
    }

    @Test
    void login_Success() throws FirebaseAuthException{
        Map<String,Object>firebaseResponse=new HashMap<>();
        firebaseResponse.put("idToken","firebase-token");
        CustomUserDetails userDetails=Mockito.mock(CustomUserDetails.class);
        Authentication authentication=Mockito.mock(Authentication.class);
        UserEntity user=new UserEntity();
        UserLoginResponseDTO userLoginResponseDTO=new UserLoginResponseDTO();

        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.eq(Map.class)))
                .thenReturn(firebaseResponse);
        Mockito.doNothing().when(firebaseAuthService).verifyToken("firebase-token");
        Mockito.when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(new UserEntity()));
        Mockito.when(userDetails.getUserEntity()).thenReturn(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        Mockito.when(modelMapperUtils.map(user, UserLoginResponseDTO.class)).thenReturn(userLoginResponseDTO);
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(authentication);

        LoginResponseDTO response=emailAuthenticationStrategy.login(requestDTO);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getJwt());
        Assertions.assertEquals("jwt-token", response.getJwt().token());

        Mockito.verify(firebaseAuthService).verifyToken("firebase-token");
        Mockito.verify(authenticationManager).authenticate(Mockito.any());
    }
    @Test
    void login_Succes_New_User() throws FirebaseAuthException{
        Map<String,Object>firebaseResponse=new HashMap<>();
        firebaseResponse.put("idToken","firebase-token");
        CustomUserDetails userDetails=Mockito.mock(CustomUserDetails.class);
        Authentication authentication=Mockito.mock(Authentication.class);
        UserEntity user=new UserEntity();
        UserLoginResponseDTO userLoginResponseDTO=new UserLoginResponseDTO();

        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.eq(Map.class)))
                .thenReturn(firebaseResponse);
        Mockito.doNothing().when(firebaseAuthService).verifyToken("firebase-token");
        Mockito.when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userService.createUser(requestDTO.getEmail(),"DEFAULT",requestDTO.getPassword(),null)).thenReturn(new UserEntity());
        Mockito.when(userDetails.getUserEntity()).thenReturn(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        Mockito.when(modelMapperUtils.map(user, UserLoginResponseDTO.class)).thenReturn(userLoginResponseDTO);
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(authentication);

        LoginResponseDTO response=emailAuthenticationStrategy.login(requestDTO);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getJwt());
        Assertions.assertEquals("jwt-token", response.getJwt().token());

        Mockito.verify(firebaseAuthService).verifyToken("firebase-token");
        Mockito.verify(authenticationManager).authenticate(Mockito.any());
        Mockito.verify(userService).createUser(requestDTO.getEmail(),"DEFAULT",requestDTO.getPassword(),null);
    }
    @Test
    void login_Firebase_Failed() throws FirebaseAuthException{
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.eq(Map.class)))
                .thenThrow(new RestClientException("firebase error"));
        Mockito.when(localizedMessageService.getMessage(Mockito.eq("firebase.auth.error"),Mockito.eq("firebase error")))
                .thenReturn("firebase.auth.error");
        AuthenticationServiceException ex=Assertions.assertThrows(AuthenticationServiceException.class,
                ()->emailAuthenticationStrategy.login(requestDTO));
        Assertions.assertNotNull(ex);
        Assertions.assertEquals("firebase.auth.error",ex.getMessage());
    }
    @Test
    void login_Failed_Token_null()throws FirebaseAuthException{
        Mockito.when(restTemplate.postForObject(Mockito.anyString(),Mockito.any(),Mockito.eq(Map.class)))
                .thenReturn(null);
        Mockito.when(localizedMessageService.getMessage(Mockito.eq("firebase.invalid_id_token")))
                .thenReturn("firebase.invalid_id_token");
        AuthenticationServiceException ex=Assertions.assertThrows(AuthenticationServiceException.class,
                ()->emailAuthenticationStrategy.login(requestDTO));
        Assertions.assertNotNull(ex);
        Assertions.assertEquals("firebase.invalid_id_token",ex.getMessage());
    }

    @Test
    void getStrategyType() {
        Assertions.assertEquals("EMAIL",emailAuthenticationStrategy.getStrategyType());
    }
}