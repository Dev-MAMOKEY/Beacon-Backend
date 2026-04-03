package com.mamoki.beacon.global.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
@Configuration
public class FcmConfig { //fcm키를 불러드리고 초기화하는 config파일
    @Value("${firebase.beacon-account.path}")
    private String SERVICE_ACCOUNT_PATH;


    @Bean
    //json파일가지고 firebase에 접속하는 Bean
    public FirebaseApp firebaseApp() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            // resource/firebase/beacon-account.json 파일 불러드림
                            GoogleCredentials.fromStream(new ClassPathResource(SERVICE_ACCOUNT_PATH).getInputStream())
                    )
                    .build();

            log.info("Successfully initialized firebase app");
            return FirebaseApp.initializeApp(options);

        } catch (IOException exception) {
            log.error("Fail to initialize firebase app{}", exception.getMessage());
            return null;
        }
    }

    @Bean //푸시알람 가능하게 하는 Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
