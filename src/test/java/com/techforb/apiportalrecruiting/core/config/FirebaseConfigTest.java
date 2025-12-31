package com.techforb.apiportalrecruiting.core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirebaseConfigTest {

    @InjectMocks
    private FirebaseConfig firebaseConfig;

    @Mock
    private GoogleCredentials mockCredentials;

    @Mock
    private FirebaseApp mockFirebaseApp;

    String originalEnvValue;

    @BeforeEach
    void setUp() {
        //guardar el valor original de la variable de entorno
        originalEnvValue = System.getenv("FIREBASE_CREDENTIALS_BASE64");

        //limpiar las apps de firebase antes de cada test
        List<FirebaseApp> apps = FirebaseApp.getApps();
        for (FirebaseApp app : apps) {
            app.delete();
        }
    }

    @AfterEach
    void tearDown() {
        //limpiar las apps de firebase después de cada test
        List<FirebaseApp> apps = FirebaseApp.getApps();
        for (FirebaseApp app : apps) {
            app.delete();
        }
    }

    @Test
    void firebaseCredentialsFromBase64_whenBlank_throwsException() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> firebaseConfig.firebaseCredentialsFromBase64("")
        );

        assertEquals("FIREBASE_CREDENTIALS_BASE64 is not set", ex.getMessage());
    }

    @Test
    void firebaseCredentialsFromBase64_whenValidBase64_returnsCredentials() throws Exception {

        String fakeJson = "{ \"type\": \"service_account\" }";
        String base64 = Base64.getEncoder().encodeToString(fakeJson.getBytes());

        try (MockedStatic<GoogleCredentials> googleCredMock =
                     mockStatic(GoogleCredentials.class)) {

            googleCredMock
                    .when(() -> GoogleCredentials.fromStream(any(InputStream.class)))
                    .thenReturn(mock(GoogleCredentials.class));

            GoogleCredentials credentials =
                    firebaseConfig.firebaseCredentialsFromBase64(base64);

            assertNotNull(credentials);

            googleCredMock.verify(
                    () -> GoogleCredentials.fromStream(any(InputStream.class)),
                    times(1)
            );
        }
    }



    @Test
    void initializeFirebase_shouldReturnExistingInstance_whenFirebaseAppAlreadyExists() {
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(FirebaseApp::getApps)
                    .thenReturn(List.of(mockFirebaseApp));
            firebaseAppMock.when(FirebaseApp::getInstance)
                    .thenReturn(mockFirebaseApp);

            FirebaseApp result = firebaseConfig.initializeFirebase(mockCredentials);

            assertNotNull(result);
            assertEquals(mockFirebaseApp, result);

            firebaseAppMock.verify(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)), never());
        }
    }

    @Test
    void initializeFirebase_shouldInitializeNewApp_whenNoFirebaseAppExists() {
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(FirebaseApp::getApps)
                    .thenReturn(Collections.emptyList());
            firebaseAppMock.when(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)))
                    .thenReturn(mockFirebaseApp);

            FirebaseApp result = firebaseConfig.initializeFirebase(mockCredentials);

            assertNotNull(result);
            assertEquals(mockFirebaseApp, result);

            firebaseAppMock.verify(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)), times(1));
        }
    }

}