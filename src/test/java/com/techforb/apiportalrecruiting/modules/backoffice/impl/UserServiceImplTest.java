package com.techforb.apiportalrecruiting.modules.backoffice.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.dtos.users.EmailChangePasswordRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.UserLoginResponseDTO;
import com.techforb.apiportalrecruiting.core.entities.Role;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.UserRepository;
import com.techforb.apiportalrecruiting.core.security.CustomUserDetails;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthenticationToken;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthService;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.services.RoleService;
import com.techforb.apiportalrecruiting.core.services.impl.UserServiceImpl;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplCoverageTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PersonRepository personRepository;
    @Mock PersonService personService;
    @Mock RoleService roleService;
    @Mock FirebaseAuthService firebaseAuthService;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock ModelMapperUtils modelMapperUtils;
    @Mock PasswordEncoder passwordEncoder;
    @Mock LocalizedMessageService localizedMessageService;
    @Mock RestTemplate restTemplate;

    // Vamos a usar spy para stubear addUserToDb
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = spy(new UserServiceImpl(
                userRepository,
                personRepository,
                roleService,
                firebaseAuthService,
                authenticationManager,
                jwtService,
                modelMapperUtils,
                passwordEncoder,
                localizedMessageService,
                restTemplate
        ));

        // Seteamos URLs internas sin depender de @Value/@PostConstruct
        setField(service, "firebaseAuthUrlWithPassword", "http://firebase/password");
        setField(service, "firebaseAuthUrlWithProvider", "http://firebase/provider");
    }

    @AfterEach
    void tearDown() {
        // Limpieza por si otros tests tocan el contexto
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    // ---------------------------
    // emailLogin: response null / missing idToken
    // ---------------------------

    @Test
    void emailLogin_shouldThrowAuthenticationServiceException_whenResponseNullOrMissingIdToken(){
        when(localizedMessageService.getMessage("firebase.invalid_id_token"))
                .thenReturn("Invalid idToken");

        // Caso: response null - mockeamos restTemplate directamente
        when(restTemplate.postForObject(eq("http://firebase/password"), anyMap(), eq(Map.class)))
                .thenReturn(null);

        EmailLoginRequestDTO req = new EmailLoginRequestDTO("user@mail.com", "pass");

        AuthenticationServiceException ex = assertThrows(AuthenticationServiceException.class,
                () -> service.emailLogin(req));

        assertTrue(ex.getMessage().contains("Invalid idToken"));
        verify(localizedMessageService).getMessage("firebase.invalid_id_token");
    }

    // ---------------------------
    // emailLogin: findByEmail empty => addUserToDb llamado (rama parcial)
    // ---------------------------

    @Test
    void emailLogin_shouldCallAddUserToDb_whenUserNotExists() throws Exception {
        Map<String, Object> firebaseResp = new HashMap<>();
        firebaseResp.put("idToken", "firebase-id-token");

        // Mockeamos restTemplate directamente
        when(restTemplate.postForObject(eq("http://firebase/password"), anyMap(), eq(Map.class)))
                .thenReturn(firebaseResp);

        doNothing().when(service).addUserToDb(anyString(), anyString(), any());

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());

        // auth mocks
        Authentication auth = mock(Authentication.class);
        CustomUserDetails cud = mock(CustomUserDetails.class);

        UserEntity userEntity = new UserEntity();
        when(cud.getUserEntity()).thenReturn(userEntity);

        when(auth.getPrincipal()).thenReturn(cud);
        when(authenticationManager.authenticate(any(FirebaseAuthenticationToken.class))).thenReturn(auth);

        when(jwtService.generateToken(cud)).thenReturn("jwt");

        when(modelMapperUtils.map(userEntity,
                UserLoginResponseDTO.class))
                .thenReturn(mock(UserLoginResponseDTO.class));

        service.emailLogin(new EmailLoginRequestDTO("user@mail.com", "pass"));

        verify(service).addUserToDb("user@mail.com", "APPLICANT", "pass");
    }


    @Test
    void emailLogin_shouldNotCallAddUserToDb_whenUserExists() throws Exception {
        Map<String, Object> firebaseResp = new HashMap<>();
        firebaseResp.put("idToken", "firebase-id-token");

        // Mockeamos restTemplate directamente
        when(restTemplate.postForObject(eq("http://firebase/password"), anyMap(), eq(Map.class)))
                .thenReturn(firebaseResp);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(new UserEntity()));

        // auth mocks
        Authentication auth = mock(Authentication.class);
        CustomUserDetails cud = mock(CustomUserDetails.class);

        UserEntity userEntity = new UserEntity();
        when(cud.getUserEntity()).thenReturn(userEntity);

        when(auth.getPrincipal()).thenReturn(cud);
        when(authenticationManager.authenticate(any(FirebaseAuthenticationToken.class))).thenReturn(auth);

        when(jwtService.generateToken(cud)).thenReturn("jwt");

        when(modelMapperUtils.map(userEntity,
                UserLoginResponseDTO.class))
                .thenReturn(mock(UserLoginResponseDTO.class));

        service.emailLogin(new EmailLoginRequestDTO("user@mail.com", "pass"));

        verify(service, never()).addUserToDb(anyString(), anyString(), any());
    }


    // ---------------------------
    // googleLogin: catch RestClientException (rama que te falta)
    // ---------------------------

    @Test
    void googleLogin_shouldThrowAuthenticationServiceException_whenFirebaseCallThrowsRestClientException() {
        // Mockeamos restTemplate para que lance RestClientException
        when(restTemplate.postForObject(eq("http://firebase/provider"), anyMap(), eq(Map.class)))
                .thenThrow(new RestClientException("firebase down"));

        GoogleLoginRequestDTO dto = new GoogleLoginRequestDTO("access-token");

        AuthenticationServiceException ex = assertThrows(AuthenticationServiceException.class,
                () -> service.googleLogin(dto));

        assertTrue(ex.getMessage().contains("Auth with firebase error"));
        assertTrue(ex.getMessage().contains("firebase down"));
    }

    // ---------------------------
    // googleLogin: response sin idToken => localized firebase.invalid_id_token
    // ---------------------------

    @Test
    void googleLogin_shouldThrowAuthenticationServiceException_whenResponseMissingIdToken() {
        when(localizedMessageService.getMessage("firebase.invalid_id_token"))
                .thenReturn("Invalid idToken");

        Map<String, Object> resp = new HashMap<>();
        resp.put("email", "user@mail.com"); // pero NO idToken

        // Mockeamos restTemplate directamente
        when(restTemplate.postForObject(eq("http://firebase/provider"), anyMap(), eq(Map.class)))
                .thenReturn(resp);

        GoogleLoginRequestDTO dto = new GoogleLoginRequestDTO("access-token");

        AuthenticationServiceException ex = assertThrows(AuthenticationServiceException.class,
                () -> service.googleLogin(dto));

        assertTrue(ex.getMessage().contains("Invalid idToken"));
        verify(localizedMessageService).getMessage("firebase.invalid_id_token");
    }

    @Test
    void googleLogin_shouldCallAddUserToDb_whenIsNewUserPresent_andUserNotExists() throws Exception {
        Map<String, Object> resp = new HashMap<>();
        resp.put("idToken", "id-token");
        resp.put("email", "user@mail.com");
        resp.put("isNewUser", true);

        // Mockeamos restTemplate directamente
        when(restTemplate.postForObject(eq("http://firebase/provider"), anyMap(), eq(Map.class)))
                .thenReturn(resp);

        doNothing().when(service).addUserToDb(anyString(), anyString(), any());

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());

        // auth mocks
        Authentication auth = mock(Authentication.class);
        CustomUserDetails cud = mock(CustomUserDetails.class);

        UserEntity userEntity = new UserEntity();
        when(cud.getUserEntity()).thenReturn(userEntity);

        when(auth.getPrincipal()).thenReturn(cud);
        when(authenticationManager.authenticate(any(FirebaseAuthenticationToken.class))).thenReturn(auth);

        when(jwtService.generateToken(cud)).thenReturn("jwt");

        when(modelMapperUtils.map(userEntity,
                UserLoginResponseDTO.class))
                .thenReturn(mock(UserLoginResponseDTO.class));

        service.googleLogin(new GoogleLoginRequestDTO("access-token"));

        verify(service).addUserToDb(eq("user@mail.com"), eq("APPLICANT"), isNull());
    }

    @Test
    void googleLogin_shouldCallAddUserToDb_whenIsNewUserMissing_andUserNotExists() throws Exception {
        Map<String, Object> resp = new HashMap<>();
        resp.put("idToken", "id-token");
        resp.put("email", "user@mail.com");
        // NO "isNewUser"

        // Mockeamos restTemplate directamente
        when(restTemplate.postForObject(eq("http://firebase/provider"), anyMap(), eq(Map.class)))
                .thenReturn(resp);

        doNothing().when(service).addUserToDb(anyString(), anyString(), any());

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());

        // auth mocks
        Authentication auth = mock(Authentication.class);
        CustomUserDetails cud = mock(CustomUserDetails.class);

        UserEntity userEntity = new UserEntity();
        when(cud.getUserEntity()).thenReturn(userEntity);

        when(auth.getPrincipal()).thenReturn(cud);
        when(authenticationManager.authenticate(any(FirebaseAuthenticationToken.class))).thenReturn(auth);

        when(jwtService.generateToken(cud)).thenReturn("jwt");

        when(modelMapperUtils.map(userEntity,
                UserLoginResponseDTO.class))
                .thenReturn(mock(UserLoginResponseDTO.class));

        service.googleLogin(new GoogleLoginRequestDTO("access-token"));

        verify(service).addUserToDb(eq("user@mail.com"), eq("APPLICANT"), isNull());
    }

    @Test
    void changePassword_shouldThrowAuthenticationServiceException_whenFirebaseCallThrowsRestClientException() {
        UserEntity existing = new UserEntity();
        existing.setEmail("user@mail.com");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(existing));

        // Mockeamos restTemplate para que lance RestClientException
        when(restTemplate.postForObject(eq("http://firebase/password"), anyMap(), eq(Map.class)))
                .thenThrow(new RestClientException("boom"));

        EmailChangePasswordRequestDTO dto = new EmailChangePasswordRequestDTO(
                "user@mail.com", "old", "newPass", "newPass"
        );

        AuthenticationServiceException ex = assertThrows(AuthenticationServiceException.class,
                () -> service.changePassword(dto));

        assertTrue(ex.getMessage().contains("Auth with firebase error"));
        assertTrue(ex.getMessage().contains("boom"));
    }

    @Test
    void createUser_shouldEncodeProvidedPassword_andAssignRole_whenRoleNameProvided() {
        when(passwordEncoder.encode("pass")).thenReturn("ENC(pass)");

        Role role = new Role();
        role.setName("APPLICANT");
        when(roleService.findByName("APPLICANT")).thenReturn(role);

        // save devuelve el mismo objeto (o uno "persistido")
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserEntity saved = service.createUser("user@mail.com", "APPLICANT", "pass", null);

        verify(passwordEncoder).encode("pass");
        verify(roleService).findByName("APPLICANT");
        verify(userRepository).save(captor.capture());
        verify(personService).createPerson(any(UserEntity.class));

        UserEntity toSave = captor.getValue();
        assertEquals("user@mail.com", toSave.getEmail());
        assertEquals("ENC(pass)", toSave.getPassword());
        assertNotNull(toSave.getRoles());
        assertEquals(1, toSave.getRoles().size());

        assertSame(toSave, saved);
    }

    @Test
    void createUser_shouldGenerateOauthPassword_whenPasswordNull_andNotAssignRole_whenRoleNameNull() {
        // Capturamos el string que se encodea para verificar que arranca con oauth_user_
        ArgumentCaptor<String> pwdCaptor = ArgumentCaptor.forClass(String.class);
        when(passwordEncoder.encode(anyString())).thenReturn("ENC(any)");

        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserEntity saved = service.createUser("user@mail.com", null, null, null);

        verify(passwordEncoder).encode(pwdCaptor.capture());
        assertTrue(pwdCaptor.getValue().startsWith("oauth_user_"));

        verify(roleService, never()).findByName(anyString());

        assertNotNull(saved.getPassword());
        assertNotNull(saved.getRoles());
        assertEquals(0, saved.getRoles().size());
    }

    // ---------------------------
    // findByEmail: simple delegación (líneas que te faltan)
    // ---------------------------

    @Test
    void findByEmail_shouldDelegateToRepository() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());

        Optional<UserEntity> result = service.findByEmail("user@mail.com");

        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail("user@mail.com");
    }

    // ---------------------------
    // Helpers: setField por reflexión (sin libs extra)
    // ---------------------------
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}