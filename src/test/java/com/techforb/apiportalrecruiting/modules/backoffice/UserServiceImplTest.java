package com.techforb.apiportalrecruiting.modules.backoffice;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.repositories.UserRepository;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthService;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.services.RoleService;
import com.techforb.apiportalrecruiting.core.dtos.users.EmailChangePasswordRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.core.services.impl.UserServiceImpl;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PersonService personService;

	@Mock
	private FirebaseAuthService firebaseAuthService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;

	@Mock
	RoleService roleService;

	@Mock
	ModelMapperUtils modelMapperUtils;

	@Mock
	LocalizedMessageService localizedMessageService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	@Spy
	private UserServiceImpl userService;

	@AfterEach
	void tearDown() {
		//para los test de getUserFromContext
		SecurityContextHolder.clearContext();
	}

	@Test
	void emailRegisterShouldAddUserToDbWhenNotExists() throws FirebaseAuthException {
		EmailLoginRequestDTO newUser = new EmailLoginRequestDTO("test@email.com", "password");
		when(firebaseAuthService.registerUser(anyString(), anyString())).thenReturn("firebase-uid");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		userService.emailRegister(newUser);

		verify(firebaseAuthService).registerUser(newUser.getEmail(), newUser.getPassword());
		verify(userRepository).findByEmail(newUser.getEmail());
		verify(userRepository).save(any(UserEntity.class));
	}

	@Test
	void emailRegisterShouldNotAddUserToDbWhenAlreadyExists() throws FirebaseAuthException {
		EmailLoginRequestDTO newUser = new EmailLoginRequestDTO("existing@email.com", "password");
		UserEntity existingUser = new UserEntity();
		when(firebaseAuthService.registerUser(anyString(), anyString())).thenReturn("firebase-uid");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

		userService.emailRegister(newUser);

		verify(userRepository, never()).save(any());
	}

	//TEST PARA INICIO DE SESION CON EMAIL Y CONTRASEÑA
	@Test
	//@Disabled
	void emailLoginShouldReturnValidResponseWhenCredentialsAreValid() throws FirebaseAuthException {
		EmailLoginRequestDTO request = new EmailLoginRequestDTO("test@email.com", "password");

		Map<String, Object> firebaseResponse = new HashMap<>();
		firebaseResponse.put("idToken", "firebase-id-token");
		firebaseResponse.put("email", request.getEmail());

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(request.getEmail());

		UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
		userLoginResponseDTO.setEmail(userEntity.getEmail());
		userLoginResponseDTO.setId(1L);

		Authentication auth = mock(Authentication.class);
		when(auth.getPrincipal()).thenReturn(new CustomUserDetails(userEntity));
		when(modelMapperUtils.map(any(UserEntity.class), eq(UserLoginResponseDTO.class)))
				.thenReturn(userLoginResponseDTO);
		when(authenticationManager.authenticate(any())).thenReturn(auth);
		when(jwtService.generateToken(any())).thenReturn("jwt-token");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());


		userService = new UserServiceImpl(userRepository, personService, roleService,
				firebaseAuthService, authenticationManager, jwtService, modelMapperUtils,
				passwordEncoder, localizedMessageService, restTemplate) {
			@Override
			protected Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
				return firebaseResponse;
			}
		};

		LoginResponseDTO response = userService.emailLogin(request);

		assertNotNull(response);
		assertEquals(userEntity.getEmail(), response.getUser().getEmail());
		assertEquals("jwt-token", response.getJwt().token());
		verify(userRepository).save(any(UserEntity.class));
	}

	@Test
	//@Disabled
	void emailLoginShouldThrowExceptionWhenFirebaseFails() {
		EmailLoginRequestDTO request = new EmailLoginRequestDTO("test@email.com", "wrong-password");

		userService = new UserServiceImpl(userRepository, personService, roleService, firebaseAuthService,
				authenticationManager, jwtService, modelMapperUtils, passwordEncoder, localizedMessageService, restTemplate) {

			@Override
			protected Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
				throw new RestClientException("Invalid credentials");
			}
		};

		assertThrows(AuthenticationServiceException.class, () -> userService.emailLogin(request));
	}

	//TEST PARA REGISTRO E INICIO DE SESION CON GOOGLE
	@Test
	//@Disabled
	void googleLoginShouldReturnValidResponseForNewUser() throws FirebaseAuthException {
		GoogleLoginRequestDTO request = new GoogleLoginRequestDTO("google-token");

		Map<String, Object> firebaseResponse = new HashMap<>();
		firebaseResponse.put("idToken", "firebase-id-token");
		firebaseResponse.put("email", "google@email.com");
		firebaseResponse.put("isNewUser", true);

		Role role = new Role();
		role.setName("USER");

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("google@email.com");

		UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
		userLoginResponseDTO.setEmail(userEntity.getEmail());
		userLoginResponseDTO.setId(1L);

		Authentication auth = mock(Authentication.class);
		when(auth.getPrincipal()).thenReturn(new CustomUserDetails(userEntity));
		when(modelMapperUtils.map(any(UserEntity.class), eq(UserLoginResponseDTO.class)))
				.thenReturn(userLoginResponseDTO);
		when(authenticationManager.authenticate(any())).thenReturn(auth);
		when(jwtService.generateToken(any())).thenReturn("jwt-token");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		userService = new UserServiceImpl(userRepository, personService, roleService,
				firebaseAuthService, authenticationManager, jwtService, modelMapperUtils,
				passwordEncoder, localizedMessageService, restTemplate) {

			@Override
			protected Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
				return firebaseResponse;
			}
		};

		LoginResponseDTO response = userService.googleLogin(request);

		assertNotNull(response);
		assertEquals(userEntity.getEmail(), response.getUser().getEmail());
		verify(userRepository).save(any(UserEntity.class));
	}

	@Test
	void disableUserShouldReturnTrueWhenUserIsEnabled() {
		Long userId = 1L;

		UserEntity loggedUser = new UserEntity();
		loggedUser.setId(userId);

		UserEntity userToDisable = new UserEntity();
		userToDisable.setId(userId);
		userToDisable.setEnabled(true);

		doReturn(loggedUser).when(userService).getUserFromContext();
		when(userRepository.findById(userId)).thenReturn(Optional.of(userToDisable));
		when(userRepository.save(any(UserEntity.class))).thenReturn(userToDisable);

		boolean result = userService.disableUser(userId);

		assertTrue(result);
		assertFalse(userToDisable.isEnabled());
		verify(userRepository).save(userToDisable);
	}

	@Test
	void disableUserShouldReturnTrueWhenUserIsAlreadyDisabled() {
		Long userId = 1L;

		UserEntity loggedUser = new UserEntity();
		loggedUser.setId(userId);

		UserEntity userToDisable = new UserEntity();
		userToDisable.setId(userId);
		userToDisable.setEnabled(false);

		doReturn(loggedUser).when(userService).getUserFromContext();
		when(userRepository.findById(userId)).thenReturn(Optional.of(userToDisable));
		when(userRepository.save(any(UserEntity.class))).thenReturn(userToDisable);
		when(userRepository.findById(userId)).thenReturn(Optional.of(userToDisable));

		boolean result = userService.disableUser(userId);

		assertTrue(result);
		verify(userRepository, never()).save(any());
	}

	@Test
	void disableUserShouldThrowUnauthorizedExceptionWhenUserHasNoPermission() {
		Long userId = 1L;

		UserEntity authenticatedUser = new UserEntity();
		authenticatedUser.setId(2L);

		UserEntity anotherUser = new UserEntity();
		anotherUser.setId(userId);

		when(userRepository.findById(userId)).thenReturn(Optional.of(anotherUser));

		doReturn(authenticatedUser).when(userService).getUserFromContext();

		when(localizedMessageService.getMessage("user.without_permissions"))
				.thenReturn("No tenés permisos para esta acción");

		assertThrows(UnauthorizedActionException.class, () -> userService.disableUser(userId));
	}

	@Test
	void changePasswordShouldThrowExceptionWhenUserNotFound() {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO("nonexistent@email.com", "oldPass", "newPass", "newPass");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.changePassword(request));
	}

	@Test
	void changePasswordShouldThrowExceptionWhenPasswordsDoNotMatch() {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO("user@email.com", "oldPass", "newPass", "differentPass");
		UserEntity user = new UserEntity();
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		assertThrows(RuntimeException.class, () -> userService.changePassword(request));
	}

	@Test
	//@Disabled
	void changePasswordShouldThrowExceptionWhenFirebaseAuthFails() {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO("user@email.com", "oldPass", "newPass", "newPass");
		UserEntity user = new UserEntity();
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		userService = new UserServiceImpl(userRepository, personService, null, firebaseAuthService, null,
				null, null, passwordEncoder, localizedMessageService, restTemplate) {
			@Override
			protected Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
				throw new AuthenticationServiceException("Auth with firebase error");
			}
		};

		assertThrows(AuthenticationServiceException.class, () -> userService.changePassword(request));
	}

	@Test
	//@Disabled
	void changePasswordShouldUpdateUserPasswordSuccessfully() throws FirebaseAuthException {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO("user@email.com", "oldPass", "newPass", "newPass");
		UserEntity user = new UserEntity();
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		userService = new UserServiceImpl(userRepository, personService, null, firebaseAuthService,
				null, null, null, passwordEncoder, localizedMessageService, restTemplate) {
			@Override
			protected Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
				return new HashMap<>();
			}
		};

		userService.changePassword(request);

		verify(passwordEncoder).encode("newPass");
		verify(firebaseAuthService).changeUserPassword("user@email.com", "newPass");
		verify(userRepository).save(user);
	}

	@Test
	void getUserFromContext_whenAuthenticatedWithValidPrincipal_shouldReturnUserEntity() {

		UserEntity expectedUser = new UserEntity();
		expectedUser.setId(1L);
		expectedUser.setEmail("test@example.com");

		CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
		when(customUserDetails.getUserEntity()).thenReturn(expectedUser);

		Authentication authentication = mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(true);
		when(authentication.getPrincipal()).thenReturn(customUserDetails);

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		UserEntity result = userService.getUserFromContext();

		assertNotNull(result);
		assertEquals(expectedUser.getId(), result.getId());
		assertEquals(expectedUser.getEmail(), result.getEmail());
	}

	@Test
	void getUserFromContext_whenAuthenticationIsNull_shouldThrowException() {
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(securityContext);

		when(localizedMessageService.getMessage("user.not_authenticated"))
				.thenReturn("Usuario no autenticado");

		AuthenticationServiceException exception = assertThrows(
				AuthenticationServiceException.class,
				() -> userService.getUserFromContext()
		);

		assertEquals("Usuario no autenticado", exception.getMessage());
	}

	@Test
	void getUserFromContext_whenNotAuthenticated_shouldThrowException() {
		// Arrange
		Authentication authentication = mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(false);

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(localizedMessageService.getMessage("user.not_authenticated"))
				.thenReturn("Usuario no autenticado");

		AuthenticationServiceException exception = assertThrows(
				AuthenticationServiceException.class,
				() -> userService.getUserFromContext()
		);

		assertEquals("Usuario no autenticado", exception.getMessage());
	}


	@Test
	void getUserFromContext_whenPrincipalIsAnonymousUser_shouldThrowException() {
		Authentication authentication = mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(true);
		when(authentication.getPrincipal()).thenReturn(new Object());

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(localizedMessageService.getMessage("user.invalid_authentication"))
				.thenReturn("Autenticación inválida");

		AuthenticationServiceException exception = assertThrows(
				AuthenticationServiceException.class,
				() -> userService.getUserFromContext()
		);

		assertEquals("Autenticación inválida", exception.getMessage());
	}

}