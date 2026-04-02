package com.mamoki.beacon.global.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String title, String body, String fcmToken) {
        log.info("Attempting to send Notification (title: {}, body: {}, fcmToken: {})", title, body, fcmToken);
        send(createMessage(title, body, fcmToken));
    }

    private void send(Message message) {
        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully send Notification: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send Notification : {}", e.getMessage());
        }
    }

    private Message createMessage(String title, String body, String fcmToken) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(fcmToken)
                .build();
    }
}
