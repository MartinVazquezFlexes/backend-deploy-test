package com.techforb.apiportalrecruiting.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private LocalizedMessageService localizedMessageService;

    private ObjectMapper objectMapper;
    private CustomAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper(); // real
        entryPoint = new CustomAuthenticationEntryPoint(objectMapper, localizedMessageService);
    }

    @Test
    void commence_shouldReturn401_withJsonApiError_andLocalizedMessage() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(localizedMessageService.getMessage("saved.vacancy.user_not_authenticated"))
                .thenReturn("User not authenticated");

        var authException = new BadCredentialsException("Unauthorized");

        // Act
        entryPoint.commence(request, response, authException);

        // Assert HTTP response
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNotNull(response.getContentType());
        assertTrue(response.getContentType().startsWith("application/json"));
        assertEquals("UTF-8", response.getCharacterEncoding());

        // Assert JSON body
        String body = response.getContentAsString();
        assertNotNull(body);
        assertFalse(body.isBlank());

        ApiError apiError = objectMapper.readValue(body, ApiError.class);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), apiError.getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), apiError.getError());
        assertEquals("User not authenticated", apiError.getMessage());
        assertNotNull(apiError.getTimestamp());
        assertFalse(apiError.getTimestamp().isBlank());

        verify(localizedMessageService, times(1))
                .getMessage("saved.vacancy.user_not_authenticated");
    }
}
