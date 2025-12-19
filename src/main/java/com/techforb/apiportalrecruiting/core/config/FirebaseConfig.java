package com.techforb.apiportalrecruiting.core.config;
import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

	@Bean
    public GoogleCredentials firebaseCredentials() throws IOException {
		ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
        return GoogleCredentials.fromStream(resource.getInputStream());
    }

    @Bean
    public FirebaseApp initializeFirebase(GoogleCredentials credentials) {

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        return FirebaseApp.initializeApp(options);
    }
}