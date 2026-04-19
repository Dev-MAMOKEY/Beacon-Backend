package com.mamoki.beacon.global.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class TotpUtil {
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SESSION_OTP_DIGITS = 4;

    public static String generateSessionOtp(String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(secret.getBytes(StandardCharsets.UTF_8));

        BigInteger hashInt = new BigInteger(1, hash);
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < SESSION_OTP_DIGITS; i++) {
            BigInteger[] divRem = hashInt.divideAndRemainder(BigInteger.valueOf(CHARSET.length()));
            code.append(CHARSET.charAt(divRem[1].intValue()));
            hashInt = divRem[0];
        }
        return code.toString();
    }
}
