package com.example.rateLimiter.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ApiKeyGenerator {
    SecureRandom secureRandom = new SecureRandom();
    public String generateRawKey(){
        byte[] keyByte = new byte[32];
        secureRandom.nextBytes(keyByte);
        return "rl_" + Base64.getUrlEncoder().withoutPadding().encodeToString(keyByte);
    }
}
