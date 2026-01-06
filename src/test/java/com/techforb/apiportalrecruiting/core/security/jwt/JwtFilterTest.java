package com.techforb.apiportalrecruiting.core.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.security.CustomUserDetails;
import com.techforb.apiportalrecruiting.core.services.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtService, userDetailsServiceImpl, objectMapper);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void optionsRequest_shouldReturn200_andContinueFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("OPTIONS");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsServiceImpl);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void noAuthorizationHeader_shouldContinueFilterChain_withoutAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsServiceImpl);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void nonBearerAuthorizationHeader_shouldContinue_withoutAuth() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Token token123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsServiceImpl);
    }

    @Test
    void validBearerToken_shouldSetAuthentication_andContinue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("token123")).thenReturn("user@mail.com");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(java.util.List.of()); // importante

        when(userDetailsServiceImpl.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
        when(jwtService.validateToken("token123", userDetails)).thenReturn(true);

        jwtFilter.doFilter(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(userDetails, auth.getPrincipal());

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService).extractUsername("token123");
        verify(jwtService).validateToken("token123", userDetails);
        verify(userDetailsServiceImpl).loadUserByUsername("user@mail.com");
    }


    @Test
    void invalidToken_shouldNotSetAuthentication_butContinue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("token123")).thenReturn("user@mail.com");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetailsServiceImpl.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
        when(jwtService.validateToken("token123", userDetails)).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);

        verify(jwtService).extractUsername("token123");
        verify(jwtService).validateToken("token123", userDetails);
        verify(userDetailsServiceImpl).loadUserByUsername("user@mail.com");
    }

    @Test
    void usernameNull_shouldNotAuthenticate_butContinue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("token123")).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);

        verify(jwtService).extractUsername("token123");
        verifyNoInteractions(userDetailsServiceImpl);
        verify(jwtService, never()).validateToken(anyString(), any());
    }

    @Test
    void alreadyAuthenticated_shouldSkipSettingAuthentication_butContinue() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                mock(org.springframework.security.core.Authentication.class)
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("token123")).thenReturn("user@mail.com");

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService).extractUsername("token123");
        verifyNoInteractions(userDetailsServiceImpl);
        verify(jwtService, never()).validateToken(anyString(), any());
    }

    @Test
    void jwtException_shouldReturn400_withJsonBody_andNotCallChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer badtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("badtoken")).thenThrow(new JwtException("Invalid token"));

        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(400, response.getStatus());
        assertEquals("application/json", response.getContentType());

        String body = response.getContentAsString();
        assertTrue(body.contains("\"error\""));
        assertTrue(body.contains("\"message\""));
        assertTrue(body.contains("JwtException") || body.contains("Jwt"));

        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void filterChainThrowsServletException_shouldReturn500_withJsonBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET"); // sin token => va directo a chain.doFilter
        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new ServletException("boom")).when(filterChain).doFilter(request, response);

        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(500, response.getStatus());
        assertEquals("application/json", response.getContentType());

        String body = response.getContentAsString();
        assertTrue(body.contains("ServletException"));
        assertTrue(body.contains("boom"));
    }
}
