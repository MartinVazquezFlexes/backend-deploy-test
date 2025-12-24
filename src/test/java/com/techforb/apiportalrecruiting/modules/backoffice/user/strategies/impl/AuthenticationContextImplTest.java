    package com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.impl;

    import com.google.firebase.auth.FirebaseAuthException;
    import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
    import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
    import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
    import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LinkedInCallbackRequestDTO;
    import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
    import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.EmailAuthenticationStrategy;
    import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.GoogleAuthenticationStrategy;
    import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.LinkedInAuthenticationStrategy;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class AuthenticationContextImplTest {
        @Mock
        private EmailAuthenticationStrategy emailStrategy;
        @Mock
        private GoogleAuthenticationStrategy googleStrategy;
        @Mock
        private LinkedInAuthenticationStrategy linkedInStrategy;
        @Mock
        private LocalizedMessageService localizedMessageService;
        @InjectMocks
        private AuthenticationContextImpl authenticationContext;

        private EmailLoginRequestDTO emailRequest;
        private GoogleLoginRequestDTO googleRequest;
        private LinkedInCallbackRequestDTO linkedInRequest;
        private LoginResponseDTO loginResponse;

        @BeforeEach
        void setUp() {
            emailRequest = new EmailLoginRequestDTO();
            googleRequest = new GoogleLoginRequestDTO();
            linkedInRequest = new LinkedInCallbackRequestDTO();
            loginResponse = new LoginResponseDTO();
        }

        @Test
        void register_EmailStrategy_Success() throws FirebaseAuthException {

            authenticationContext.register("EMAIL", emailRequest);

            verify(emailStrategy, times(1)).register(emailRequest);
            verify(googleStrategy, never()).register(any());
            verify(linkedInStrategy, never()).register(any());
        }

        @Test
        void register_GoogleStrategy_Success() throws FirebaseAuthException {
            // When
            authenticationContext.register("GOOGLE", googleRequest);

            // Then
            verify(googleStrategy, times(1)).register(googleRequest);
            verify(emailStrategy, never()).register(any());
            verify(linkedInStrategy, never()).register(any());
        }

        @Test
        void register_LinkedInStrategy_Success() throws FirebaseAuthException {
            authenticationContext.register("LINKEDIN", linkedInRequest);

            verify(linkedInStrategy, times(1)).register(linkedInRequest);
            verify(emailStrategy, never()).register(any());
            verify(googleStrategy, never()).register(any());
        }

        @Test
        void register_InvalidGoogleRequest_ThrowsIllegalArgumentException() throws FirebaseAuthException {
            String invalidRequest = "invalid request type";
            String errorMessage = "Invalid google request";
            when(localizedMessageService.getMessage("auth.strategy.google.invalid_request"))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.register("GOOGLE", invalidRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
            verify(googleStrategy, never()).register(any());
        }

        @Test
        void register_InvalidLinkedinRequest_ThrowsIllegalArgumentException() throws FirebaseAuthException {
            String invalidRequest = "invalid request type";
            String errorMessage = "Invalid linkedin request";
            when(localizedMessageService.getMessage("auth.strategy.linkedin.invalid_request"))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.register("LINKEDIN", invalidRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
            verify(googleStrategy, never()).register(any());
        }

        @Test
        void register_InvalidEmailRequest_ThrowsIllegalArgumentException() throws FirebaseAuthException {
            String invalidRequest = "invalid request type";
            String errorMessage = "Invalid email request";
            when(localizedMessageService.getMessage("auth.strategy.email.invalid_request"))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.register("EMAIL", invalidRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
            verify(emailStrategy, never()).register(any());
        }

        @Test
        void register_UnsupportedStrategy_ThrowsIllegalArgumentException() {
            String unsupportedStrategy = "FACEBOOK";
            String errorMessage = "Unsupported strategy: FACEBOOK";
            when(localizedMessageService.getMessage("auth.strategy.unsupported", unsupportedStrategy))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.register(unsupportedStrategy, emailRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
        }

        @Test
        void register_EmailStrategy_FirebaseAuthException_Propagated() throws FirebaseAuthException {
            RuntimeException firebaseException = new RuntimeException("Firebase error");
            doThrow(firebaseException).when(emailStrategy).register(emailRequest);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> authenticationContext.register("EMAIL", emailRequest)
            );

            assertEquals("Firebase error", exception.getMessage());
            verify(emailStrategy, times(1)).register(emailRequest);
        }


        @Test
        void login_EmailStrategy_Success() throws FirebaseAuthException {
            when(emailStrategy.login(emailRequest)).thenReturn(loginResponse);

            LoginResponseDTO result = authenticationContext.login("EMAIL", emailRequest);

            assertNotNull(result);
            assertEquals(loginResponse, result);
            verify(emailStrategy, times(1)).login(emailRequest);
        }

        @Test
        void login_GoogleStrategy_Success() throws FirebaseAuthException {
            when(googleStrategy.login(googleRequest)).thenReturn(loginResponse);

            LoginResponseDTO result = authenticationContext.login("GOOGLE", googleRequest);

            assertNotNull(result);
            assertEquals(loginResponse, result);
            verify(googleStrategy, times(1)).login(googleRequest);
        }
        @Test
        void login_LinkedinStrategy_Success() throws FirebaseAuthException{
            when(linkedInStrategy.login(linkedInRequest)).thenReturn(loginResponse);

            LoginResponseDTO result=authenticationContext.login("LINKEDIN",linkedInRequest);

            assertNotNull(result);
            assertEquals(loginResponse, result);
            verify(linkedInStrategy,times(1)).login(linkedInRequest);

        }
        @Test
        void login_InvalidEmailRequest_ThrowsIllegalArgumentException() throws FirebaseAuthException{
            String invalidRequest = "invalid request";
            String errorMessage = "Invalid Email request";
            when(localizedMessageService.getMessage("auth.strategy.email.invalid_request"))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.login("EMAIL", invalidRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
            verify(emailStrategy, never()).login(any());
        }
        @Test
        void login_InvalidLinkedinRequest_ThrowsIllegalArgumentException() throws FirebaseAuthException{
            String invalidRequest = "invalid request";
            String errorMessage = "Invalid Linkedin request";
            when(localizedMessageService.getMessage("auth.strategy.linkedin.invalid_request"))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.login("LINKEDIN", invalidRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
            verify(emailStrategy, never()).login(any());
        }

        @Test
        void login_InvalidGoogleRequest_ThrowsIllegalArgumentException() throws FirebaseAuthException {
            String invalidRequest = "invalid request";
            String errorMessage = "Invalid Google request";
            when(localizedMessageService.getMessage("auth.strategy.google.invalid_request"))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.login("GOOGLE", invalidRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
            verify(googleStrategy, never()).login(any());
        }

        @Test
        void login_UnsupportedStrategy_ThrowsIllegalArgumentException() {
            String unsupportedStrategy = "TWITTER";
            String errorMessage = "Unsupported strategy: TWITTER";
            when(localizedMessageService.getMessage("auth.strategy.unsupported", unsupportedStrategy))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.login(unsupportedStrategy, emailRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
        }


        @Test
        void register_CaseInsensitiveEmail_Success() throws FirebaseAuthException {
            authenticationContext.register("email", emailRequest);

            verify(emailStrategy, times(1)).register(emailRequest);
        }

        @Test
        void register_CaseInsensitiveEmailWithWrongType_ThrowsException() throws FirebaseAuthException {
            String errorMessage = "Invalid email request";
            when(localizedMessageService.getMessage("auth.strategy.email.invalid_request"))
                    .thenReturn(errorMessage);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationContext.register("email", googleRequest)
            );

            assertEquals(errorMessage, exception.getMessage());
            verify(emailStrategy, never()).register(any());
        }
    }