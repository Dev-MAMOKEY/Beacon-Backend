package com.mamoki.beacon.global.fcm.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String title, String body, String fcmToken) {
        log.info("Attempting to send Notification (title: {}, body: {}, fcmToken: {})", title, body, fcmToken);
        send(createMessage(title, body, fcmToken));
    }

    //Multicast로 여러명에게 알람 발송
    public void sendMultiNotification(String title, String body, List<String> fcmTokens){
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(fcmTokens)
                .build();
        try{
            BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);
            log.info("성공: {}, 실패: {}", response.getSuccessCount(), response.getFailureCount());
        }
        catch (FirebaseMessagingException e){
            log.error("FCM 멀티캐스트 발송 실패: {}", e.getMessage());
        }
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
