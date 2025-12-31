package com.techforb.apiportalrecruiting.core.security.linkedin.impl;

import com.techforb.apiportalrecruiting.core.config.LinkedInConfig;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkedinServiceImplTest {

    @Mock
    private LinkedInConfig linkedInConfig;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private LocalizedMessageService localizedMessageService;

    @InjectMocks
    private LinkedinServiceImpl linkedinService;

    private static final String CLIENT_ID = "test_client_id";
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final String SCOPE = "openid profile email";
    private static final String CLIENT_SECRET = "test_client_secret";
    private static final String TEST_CODE = "test_code";
    private static final String TEST_TOKEN = "test_access_token";
    private static final String TEST_STATE = "test_state";

    @BeforeEach
    void setUp() {
        linkedinService.initHeaders();
        assertNotNull(linkedinService.formHeaders);
        assertNotNull(linkedinService.jsonHeaders);
    }

    @Test
    void getAuthorizationUrl_ValidState_ReturnsCorrectUrl() {
        when(linkedInConfig.getClientId()).thenReturn(CLIENT_ID);
        when(linkedInConfig.getRedirectUri()).thenReturn(REDIRECT_URI);
        when(linkedInConfig.getScope()).thenReturn(SCOPE);

        String result = linkedinService.getAuthorizationUrl(TEST_STATE);

        String expectedUrl = String.format(
                "https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                CLIENT_ID,
                REDIRECT_URI,
                SCOPE.replace(" ", "%20"),
                TEST_STATE
        );

        assertEquals(expectedUrl, result);
    }

    @Test
    void getAuthorizationUrl_NullState_ThrowsException() {
        when(localizedMessageService.getMessage("auth.linkedin.state_required"))
                .thenReturn("State is required");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> linkedinService.getAuthorizationUrl(null)
        );

        assertEquals("State is required", exception.getMessage());
        verify(localizedMessageService).getMessage("auth.linkedin.state_required");
    }

    @Test
    void getAuthorizationUrl_EmptyState_ThrowsException() {
        when(localizedMessageService.getMessage("auth.linkedin.state_required"))
                .thenReturn("State is required");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> linkedinService.getAuthorizationUrl("   ")
        );

        assertEquals("State is required", exception.getMessage());
    }

    @Test
    void getAccessToken_ValidCode_ReturnsToken() {

        when(linkedInConfig.getRedirectUri()).thenReturn(REDIRECT_URI);
        when(linkedInConfig.getClientId()).thenReturn(CLIENT_ID);
        when(linkedInConfig.getClientSecret()).thenReturn(CLIENT_SECRET);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token", TEST_TOKEN);
        responseBody.put("token_type", "Bearer");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.exchange(
                eq("https://www.linkedin.com/oauth/v2/accessToken"),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        String result = linkedinService.getAccessToken(TEST_CODE);

        assertEquals(TEST_TOKEN, result);
        verify(restTemplate).exchange(
                eq("https://www.linkedin.com/oauth/v2/accessToken"),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getAccessToken_NullCode_ThrowsException() {
        when(localizedMessageService.getMessage("auth.linkedin.code_required"))
                .thenReturn("Code is required");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> linkedinService.getAccessToken(null)
        );

        assertEquals("Code is required", exception.getMessage());
    }

    @Test
    void getAccessToken_EmptyCode_ThrowsException() {
        when(localizedMessageService.getMessage("auth.linkedin.code_required"))
                .thenReturn("Code is required");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> linkedinService.getAccessToken("")
        );

        assertEquals("Code is required", exception.getMessage());
    }

    @Test
    void getAccessToken_NoAccessTokenInResponse_ThrowsException() {
        when(linkedInConfig.getRedirectUri()).thenReturn(REDIRECT_URI);
        when(linkedInConfig.getClientId()).thenReturn(CLIENT_ID);
        when(linkedInConfig.getClientSecret()).thenReturn(CLIENT_SECRET);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "invalid_grant");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.exchange(
                eq("https://www.linkedin.com/oauth/v2/accessToken"),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);
        when(localizedMessageService.getMessage(eq("auth.linkedin.token_error"), any(Object.class)))
                .thenReturn("Token error occurred");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> linkedinService.getAccessToken(TEST_CODE)
        );

        assertEquals("Token error occurred", exception.getMessage());
    }

    @Test
    void getAccessToken_RestClientException_ThrowsException() {
        when(linkedInConfig.getRedirectUri()).thenReturn(REDIRECT_URI);
        when(linkedInConfig.getClientId()).thenReturn(CLIENT_ID);
        when(linkedInConfig.getClientSecret()).thenReturn(CLIENT_SECRET);

        when(restTemplate.exchange(
                eq("https://www.linkedin.com/oauth/v2/accessToken"),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Connection error"));

        when(localizedMessageService.getMessage(eq("auth.linkedin.token_fetch_error"), any(Object.class)))
                .thenReturn("Error fetching token");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> linkedinService.getAccessToken(TEST_CODE)
        );

        assertEquals("Error fetching token", exception.getMessage());
    }

    @Test
    void getUserProfile_ValidToken_ReturnsUserInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sub", "12345");
        userInfo.put("name", "John Doe");
        userInfo.put("email", "john.doe@example.com");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(userInfo);

        when(restTemplate.exchange(
                eq("https://api.linkedin.com/v2/userinfo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        Map<String, Object> result = linkedinService.getUserProfile(TEST_TOKEN);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.get("email"));
        assertEquals("John Doe", result.get("name"));
        assertEquals("12345", result.get("sub"));
        verify(restTemplate).exchange(
                eq("https://api.linkedin.com/v2/userinfo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getUserProfile_NullToken_ThrowsException() {
        when(localizedMessageService.getMessage("auth.linkedin.token_required"))
                .thenReturn("Token is required");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> linkedinService.getUserProfile(null)
        );

        assertEquals("Token is required", exception.getMessage());
    }

    @Test
    void getUserProfile_EmptyToken_ThrowsException() {
        when(localizedMessageService.getMessage("auth.linkedin.token_required"))
                .thenReturn("Token is required");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> linkedinService.getUserProfile("  ")
        );

        assertEquals("Token is required", exception.getMessage());
    }

    @Test
    void getUserProfile_RestClientException_ThrowsException() {
        when(restTemplate.exchange(
                eq("https://api.linkedin.com/v2/userinfo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RestClientException("API error"));

        when(localizedMessageService.getMessage(eq("auth.linkedin.profile_error"), any(Object.class)))
                .thenReturn("Error getting profile");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> linkedinService.getUserProfile(TEST_TOKEN)
        );

        assertEquals("Error getting profile", exception.getMessage());
    }

    @Test
    void getUserEmail_ValidToken_ReturnsEmail() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", "john.doe@example.com");
        userInfo.put("name", "John Doe");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(userInfo);

        when(restTemplate.exchange(
                eq("https://api.linkedin.com/v2/userinfo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        String result = linkedinService.getUserEmail(TEST_TOKEN);

        assertEquals("john.doe@example.com", result);
        verify(restTemplate).exchange(
                eq("https://api.linkedin.com/v2/userinfo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getUserEmail_NullToken_ThrowsException() {
        when(localizedMessageService.getMessage("auth.linkedin.token_required"))
                .thenReturn("Token is required");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> linkedinService.getUserEmail(null)
        );

        assertEquals("Token is required", exception.getMessage());
    }

    @Test
    void getUserEmail_NoEmailInUserInfo_ThrowsException() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", "John Doe");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(userInfo);

        when(restTemplate.exchange(
                eq("https://api.linkedin.com/v2/userinfo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        when(localizedMessageService.getMessage("auth.linkedin.email_not_found"))
                .thenReturn("Email not found");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> linkedinService.getUserEmail(TEST_TOKEN)
        );

        assertEquals("Email not found", exception.getMessage());
    }
    @Test
    void getUserEmail_GetProfileThrowsException_ThrowsException() {
        when(restTemplate.exchange(
                eq("https://api.linkedin.com/v2/userinfo"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("API error"));

        when(localizedMessageService.getMessage("auth.linkedin.profile_error", "API error"))
                .thenReturn("Error getting profile");

        when(localizedMessageService.getMessage("auth.linkedin.email_not_found"))
                .thenReturn("Email not found");

        when(localizedMessageService.getMessage("auth.linkedin.email_fetch_error", "Error getting profile"))
                .thenReturn("Error fetching email");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> linkedinService.getUserEmail(TEST_TOKEN)
        );

        assertEquals("Error fetching email", exception.getMessage());
    }
}