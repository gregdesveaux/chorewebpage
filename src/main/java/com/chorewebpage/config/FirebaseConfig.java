package com.chorewebpage.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private final FirebaseProperties firebaseProperties;

    public FirebaseConfig(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        if (!StringUtils.hasText(firebaseProperties.getServiceAccountPath())) {
            throw new IllegalStateException("Firebase service account path must be configured when firebase is enabled");
        }

        try (FileInputStream serviceAccount = new FileInputStream(firebaseProperties.getServiceAccountPath())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Initialized Firebase app for messaging");
            return FirebaseMessaging.getInstance(app);
        }
    }
}
