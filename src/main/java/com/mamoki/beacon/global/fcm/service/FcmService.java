package com.mamoki.beacon.global.fcm.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;

    //단일알람 함수
    public void sendNotification(String title, String body, String fcmToken) {
        log.info("Attempting to send Notification (title: {}, body: {}, fcmToken: {})", title, body, fcmToken);
        send(createMessage(title, body, fcmToken));
    }

    //Multicast로 여러명에게 알람 발송
    public void sendMultiNotification(String title, String body, List<String> fcmTokens){
        log.info("Attempting to send MultiNotification (title: {}, body: {}, tokenCount: {})", title, body, fcmTokens.size());
        for (int i = 0; i < fcmTokens.size(); i++) {
            String t = fcmTokens.get(i);
            log.info("  token[{}] (len={}) prefix={}", i,
                    t == null ? 0 : t.length(),
                    t == null ? "null" : t.substring(0, Math.min(20, t.length())));
        }
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(fcmTokens)
                .build();
        sendMulti(multicastMessage);
    }

    private void send(Message message) {
        try {
            String response = firebaseMessaging.send(message);
            log.info("단일 발송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 단일 발송 실패: {}", e.getMessage());
        }
    }

    private void sendMulti(MulticastMessage message){
        try{
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            log.info("성공: {}, 실패: {}", response.getSuccessCount(), response.getFailureCount());

            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                SendResponse r = responses.get(i);
                if (r.isSuccessful()) {
                    log.info("  [{}] 성공 messageId={}", i, r.getMessageId());
                } else {
                    FirebaseMessagingException ex = r.getException();
                    log.warn("  [{}] 실패 errorCode={}, messagingErrorCode={}, message={}", i,
                            ex.getErrorCode(),
                            ex.getMessagingErrorCode(),
                            ex.getMessage());
                }
            }
        }
        catch (FirebaseMessagingException e){
            log.error("FCM 멀티캐스트 발송 실패: {}", e.getMessage());
        }
    }

    //단일 메세지를 위한 메세지 생성함수
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
