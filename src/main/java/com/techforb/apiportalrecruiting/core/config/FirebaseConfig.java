package com.techforb.apiportalrecruiting.core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class FirebaseConfig {

    @Bean
    public GoogleCredentials firebaseCredentials() throws IOException {
        String base64 = System.getenv("FIREBASE_CREDENTIALS_BASE64");

        if (base64 == null || base64.isBlank()) {
            throw new IllegalStateException("FIREBASE_CREDENTIALS_BASE64 is not set");
        }

        byte[] decoded = Base64.getDecoder().decode(base64);
        InputStream serviceAccount =
                new ByteArrayInputStream(decoded);

        return GoogleCredentials.fromStream(serviceAccount);
    }

    @Bean
    public FirebaseApp initializeFirebase(GoogleCredentials credentials) {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }
}