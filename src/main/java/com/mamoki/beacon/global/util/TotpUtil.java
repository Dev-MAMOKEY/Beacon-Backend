package com.mamoki.beacon.global.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class TotpUtil {
    private static final long PERIOD = 86400L; // 24시간 (초 단위)
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int DIGITS = 6;

    public static String generateTotp(String psk) throws Exception {

        long counter = Instant.now().getEpochSecond() / PERIOD; //시간별로 초대코드 바꾸기 위해

        byte[] data = ByteBuffer.allocate(8).putLong(counter).array(); //HMAC을 쓰기위해 8 크기의 배열로 선언함 (Long은 8바이트라 8로 선언)

        Mac mac = Mac.getInstance("HmacSHA256"); //해시256으로 MAC생성하는걸로 암
        mac.init(new SecretKeySpec(psk.getBytes(StandardCharsets.UTF_8), "HmacSHA256")); //동아리별 psk를 HMAC키로 설정
        byte[] hash = mac.doFinal(data); //counter 데이터를 psk로 서명 → 32바이트 해시 생성 (32바이트는 HMAC-SHA256의 고정 출력 크기)

        BigInteger hashInt = new BigInteger(1, hash); // 해시값을 36진수로 변환해서 6자리 영숫자 코드 생성
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < DIGITS; i++) { //6자리 totp라 6번 반복
            BigInteger[] divRem = hashInt.divideAndRemainder(BigInteger.valueOf(CHARSET.length()));
            code.append(CHARSET.charAt(divRem[1].intValue()));
            hashInt = divRem[0];
        }
        return code.toString();
    }
    public static boolean verifyTotp(String psk, String inputCode) throws Exception { //사용자가 보낸 totp검증하는 함수
        return generateTotp(psk).equals(inputCode.toUpperCase()); //사용자가 소문자로 보내도 대문자로 받아줌
    }
}
