package com.techforb.apiportalrecruiting.core.security.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirebaseAuthenticationProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseToken firebaseToken;

    @Mock
    private FirebaseAuthenticationToken authenticationToken;

    @InjectMocks
    private FirebaseAuthenticationProvider firebaseAuthenticationProvider;

    private String idToken;
    private String email;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        idToken = "valid-firebase-token";
        email = "test@example.com";

        userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @Test
    void authenticate_ShouldReturnAuthentication_WhenTokenIsValid() throws FirebaseAuthException {
        when(authenticationToken.getCredentials()).thenReturn(idToken);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

            Authentication result = firebaseAuthenticationProvider.authenticate(authenticationToken);

            assertNotNull(result);
            assertEquals(userDetails, result.getPrincipal());
            assertNull(result.getCredentials());
            assertIterableEquals(userDetails.getAuthorities(), result.getAuthorities());
            assertTrue(result.isAuthenticated());
        }

        verify(userDetailsService).loadUserByUsername(email);
    }


    @Test
    void supports_ShouldReturnTrue_WhenAuthenticationIsFirebaseAuthenticationToken() {
        boolean result = firebaseAuthenticationProvider.supports(FirebaseAuthenticationToken.class);

        assertTrue(result);
    }

    @Test
    void supports_ShouldReturnFalse_WhenAuthenticationIsNotFirebaseAuthenticationToken() {
        boolean result = firebaseAuthenticationProvider.supports(Authentication.class);

        assertFalse(result);
    }
}