package com.techforb.apiportalrecruiting.modules.backoffice.user;

import org.springframework.security.authentication.AuthenticationServiceException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.security.jwt.Jwt;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.security.linkedin.LinkedInService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailChangePasswordRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LinkedInCallbackRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.UserLoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.impl.UserDetailsServiceImpl;
import com.techforb.apiportalrecruiting.modules.backoffice.user.impl.UserServiceImpl;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.AuthenticationContextService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private AuthenticationContextService authenticationContext;

	@MockitoBean
	private LinkedInService linkedInService;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private LocalizedMessageService localizedMessageService;

	@MockitoBean
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private final ObjectMapper objectMapper = new ObjectMapper();

	// TEST PARA RUTA auth/email-register
	@Test
	void emailRegisterShouldReturn201WhenValidRequest() throws Exception {
		EmailLoginRequestDTO request = new EmailLoginRequestDTO("test@email.com", "password123");
		willDoNothing().given(userService).emailRegister(any(EmailLoginRequestDTO.class));

		mockMvc.perform(post("/api/auth/email-register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(content().string(""));

		verify(authenticationContext).register(eq("EMAIL"), any(EmailLoginRequestDTO.class));
	}

	@Test
	void emailRegisterShouldReturn400WhenInvalidRequest() throws Exception {
		String invalidRequest = "{\"password\":\"password123\"}";

		mockMvc.perform(post("/api/auth/email-register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidRequest))
				.andExpect(status().isBadRequest());
	}

	// TEST PARA RUTA auth/email-login
	@Test
	void emailLoginShouldReturn200AndTokenWhenValidCredentials() throws Exception {
		EmailLoginRequestDTO request = new EmailLoginRequestDTO("test@email.com", "password123");
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("test@email.com");
		LoginResponseDTO response = new LoginResponseDTO(
				new UserLoginResponseDTO(userEntity.getId(), userEntity.getEmail()),
				new Jwt("jwt-token"));
		given(authenticationContext.login(any(String.class), any(EmailLoginRequestDTO.class)))
				.willReturn(response);

		mockMvc.perform(post("/api/auth/email-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user.email").value("test@email.com"))
				.andExpect(jsonPath("$.jwt.token").value("jwt-token"));
	}

	@Test
	void emailLoginShouldReturn500WhenInvalidCredentials() throws Exception {
		EmailLoginRequestDTO request = new EmailLoginRequestDTO("wrong@email.com", "wrongpass");

		given(authenticationContext.login(any(String.class), any(EmailLoginRequestDTO.class)))
				.willThrow(new AuthenticationServiceException("Invalid credentials"));

		mockMvc.perform(post("/api/auth/email-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isInternalServerError());
	}

	// TEST PARA RUTA /auth/google-login
	@Test
	void googleLoginShouldReturn200WhenValidToken() throws Exception {
		GoogleLoginRequestDTO request = new GoogleLoginRequestDTO("google-oauth-token");
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("google@email.com");
		LoginResponseDTO response = new LoginResponseDTO(
				new UserLoginResponseDTO(userEntity.getId(), userEntity.getEmail()),
				new Jwt("jwt-token"));

		// coment
		given(authenticationContext.login(any(String.class), any(GoogleLoginRequestDTO.class)))
				.willReturn(response);

		mockMvc.perform(post("/api/auth/google-login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user.email").value("google@email.com"))
				.andExpect(jsonPath("$.jwt.token").value("jwt-token"));
	}

	@Test
	void changePasswordShouldReturn200WhenValidRequest() throws Exception {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO();
		request.setEmail("test@email.com");
		request.setOldPassword("OldPassword123");
		request.setNewPassword("newPassword123");
		request.setConfirmPassword("newPassword123");

		willDoNothing().given(userService).changePassword(request);

		mockMvc.perform(post("/api/change-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());

		verify(userService).changePassword(any(EmailChangePasswordRequestDTO.class));
	}

	@Test
	void changePasswordShouldReturn400WhenInvalidPasswordSize() throws Exception {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO();
		request.setEmail("test@email.com");
		request.setOldPassword("passwo");
		request.setNewPassword("newP");
		request.setConfirmPassword("newP");

		willDoNothing().given(userService).changePassword(request);

		mockMvc.perform(post("/api/change-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().is4xxClientError());

	}

	@Test
	void changePasswordShouldReturn400WhenMandatoryEmail() throws Exception {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO();
		request.setOldPassword("passwo");
		request.setNewPassword("newP");
		request.setConfirmPassword("newP");

		willDoNothing().given(userService).changePassword(request);

		mockMvc.perform(post("/api/change-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().is4xxClientError());

	}

	@Test
	void changePasswordShouldReturn400WhenInvalidRequest() throws Exception {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO();
		request.setEmail("test@email.com");
		request.setOldPassword("password123");
		request.setNewPassword("newPassword123");

		mockMvc.perform(post("/api/change-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void changePasswordShouldReturn500WhenServiceThrowsException() throws Exception {
		EmailChangePasswordRequestDTO request = new EmailChangePasswordRequestDTO();
		request.setEmail("test@email.com");
		request.setOldPassword("password123");
		request.setNewPassword("newPassword123");
		request.setConfirmPassword("newPassword123");

		willThrow(new AuthenticationServiceException("Auth with firebase error"))
				.given(userService)
				.changePassword(any(EmailChangePasswordRequestDTO.class));

		mockMvc.perform(post("/api/change-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	void disableUserShouldReturn200WhenUserIsDisabled() throws Exception {
		Long userId = 1L;

		given(userService.disableUser(userId)).willReturn(true);

		mockMvc.perform(patch("/api/user-disable/{id}", userId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		verify(userService).disableUser(userId);
	}

	@Test
	void disableUserShouldReturn403WhenUserNotAuthorized() throws Exception {
		Long userId = 2L;

		given(userService.disableUser(userId))
				.willThrow(new UnauthorizedActionException("user without permissions to perform this action"));

		mockMvc.perform(patch("/api/user-disable/{id}", userId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("user without permissions to perform this action"));
	}

	@Test
	void disableUserShouldReturn404WhenUserNotFound() throws Exception {
		Long userId = 99L;

		given(userService.disableUser(userId)).willThrow(new EntityNotFoundException("User not fount"));

		mockMvc.perform(patch("/api/user-disable/{id}", userId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("User not fount"));
	}

	// ===================================================================
	// ===================== TESTS PARA LINKEDIN =========================
	// ===================================================================

	@Test
	void getLinkedInAuthorizationUrl_shouldReturn200AndUrl() throws Exception {
		String expectedUrl = "https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=...&redirect_uri=...&state=...&scope=...";
		given(linkedInService.getAuthorizationUrl(anyString())).willReturn(expectedUrl);

		mockMvc.perform(post("/api/auth/linkedin/authorize"))
				.andExpect(status().isOk())
				.andExpect(content().string(expectedUrl));

		verify(linkedInService).getAuthorizationUrl(anyString());
	}

	@Test
	void linkedInCallback_shouldRedirectToFrontendOnSuccess() throws Exception {
		String code = "auth_code_from_linkedin";
		String state = UUID.randomUUID().toString();
		String encodedData = "encodedJwtAndUserData";
		String expectedRedirectUrl = "http://localhost:4200/#" + encodedData;

		LoginResponseDTO loginResponse = new LoginResponseDTO(
				new UserLoginResponseDTO(1L, "linkedin.user@example.com"),
				new Jwt("jwt-from-linkedin-login"));

		given(authenticationContext.login(eq("LINKEDIN"), any(LinkedInCallbackRequestDTO.class)))
				.willReturn(loginResponse);

		given(jwtService.encodeAuthData(
				eq(loginResponse.getJwt().token()),
				eq(loginResponse.getUser().getEmail()),
				eq(loginResponse.getUser().getId()))).willReturn(encodedData);

		mockMvc.perform(get("/api/auth/linkedin/callback")
				.param("code", code)
				.param("state", state))
				.andExpect(status().isFound()) // 302 Redirect
				.andExpect(redirectedUrl(expectedRedirectUrl));

		verify(authenticationContext).login(eq("LINKEDIN"), any(LinkedInCallbackRequestDTO.class));
		verify(jwtService).encodeAuthData(anyString(), anyString(), anyLong());
	}

	@Test
	void linkedInCallback_shouldReturn400WhenCodeIsMissing() throws Exception {
		given(localizedMessageService.getMessage("auth.linkedin.code_required"))
				.willReturn("Authorization code is required from LinkedIn.");

		mockMvc.perform(get("/api/auth/linkedin/callback"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void linkedInCallback_shouldReturn500OnProcessingError() throws Exception {
		String code = "auth_code_from_linkedin";
		String state = UUID.randomUUID().toString();

		given(authenticationContext.login(eq("LINKEDIN"), any(LinkedInCallbackRequestDTO.class)))
				.willThrow(new RuntimeException("Failed to process LinkedIn token"));
		given(localizedMessageService.getMessage("auth.linkedin.processing_error"))
				.willReturn("An error occurred while processing the LinkedIn authentication.");

		mockMvc.perform(get("/api/auth/linkedin/callback")
				.param("code", code)
				.param("state", state))
				.andExpect(status().isInternalServerError());
	}
}