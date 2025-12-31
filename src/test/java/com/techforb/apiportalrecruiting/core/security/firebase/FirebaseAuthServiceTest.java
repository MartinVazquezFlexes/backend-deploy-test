package com.techforb.apiportalrecruiting.core.security.firebase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirebaseAuthServiceTest {

    @InjectMocks
    private FirebaseAuthService firebaseAuthService;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private UserRecord userRecord;

    @Test
    void registerUser_shouldReturnUid_whenUserIsCreatedSuccessfully() throws FirebaseAuthException {
        String email = "test@example.com";
        String password = "password123";
        String expectedUid = "test-uid-123";

        when(userRecord.getUid()).thenReturn(expectedUid);
        when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class))).thenReturn(userRecord);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            String actualUid = firebaseAuthService.registerUser(email, password);

            assertEquals(expectedUid, actualUid);
            verify(firebaseAuth).createUser(any(UserRecord.CreateRequest.class));
        }
    }



    @Test
    void verifyToken_shouldNotThrowException_whenTokenIsValid() throws FirebaseAuthException {
        String validToken = "valid-token-123";

        when(firebaseAuth.verifyIdToken(validToken)).thenReturn(null);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            assertDoesNotThrow(() -> firebaseAuthService.verifyToken(validToken));
            verify(firebaseAuth).verifyIdToken(validToken);
        }
    }



    @Test
    void changeUserPassword_shouldUpdatePassword_whenUserExists() throws FirebaseAuthException {
        String email = "test@example.com";
        String newPassword = "newPassword123";
        String userId = "user-id-123";

        when(userRecord.getUid()).thenReturn(userId);
        when(firebaseAuth.getUserByEmail(email)).thenReturn(userRecord);
        when(firebaseAuth.updateUser(any(UserRecord.UpdateRequest.class))).thenReturn(userRecord);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            assertDoesNotThrow(() -> firebaseAuthService.changeUserPassword(email, newPassword));

            verify(firebaseAuth).getUserByEmail(email);
            verify(firebaseAuth).updateUser(any(UserRecord.UpdateRequest.class));
        }
    }

    @Test
    void registerUser_shouldThrowException_whenFirebaseAuthFails() throws FirebaseAuthException {
        String email = "test@example.com";
        String password = "password123";

        FirebaseAuthException exception = mock(FirebaseAuthException.class);

        when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class)))
                .thenThrow(exception);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            assertThrows(FirebaseAuthException.class, () -> {
                firebaseAuthService.registerUser(email, password);
            });

            verify(firebaseAuth).createUser(any(UserRecord.CreateRequest.class));
        }
    }

    @Test
    void verifyToken_shouldThrowException_whenTokenIsInvalid() throws FirebaseAuthException {
        String invalidToken = "invalid-token";

        FirebaseAuthException exception = mock(FirebaseAuthException.class);

        when(firebaseAuth.verifyIdToken(invalidToken))
                .thenThrow(exception);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            assertThrows(FirebaseAuthException.class, () -> {
                firebaseAuthService.verifyToken(invalidToken);
            });

            verify(firebaseAuth).verifyIdToken(invalidToken);
        }
    }

    @Test
    void changeUserPassword_shouldThrowException_whenUserNotFound() throws FirebaseAuthException {
        String email = "nonexistent@example.com";
        String newPassword = "newPassword123";

        FirebaseAuthException exception = mock(FirebaseAuthException.class);

        when(firebaseAuth.getUserByEmail(email))
                .thenThrow(exception);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

            assertThrows(FirebaseAuthException.class, () -> {
                firebaseAuthService.changeUserPassword(email, newPassword);
            });

            verify(firebaseAuth).getUserByEmail(email);
            verify(firebaseAuth, never()).updateUser(any(UserRecord.UpdateRequest.class));
        }
    }
}