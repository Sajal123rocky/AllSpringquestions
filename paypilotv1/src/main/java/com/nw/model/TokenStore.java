package com.nw.model;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;



@Component
public class TokenStore {

    private ConcurrentHashMap<String, TokenDetails> tokenMap = new ConcurrentHashMap<>();

    // Store the token with expiration time
    public void put(String email, String token, LocalDateTime expirationTime) {
        tokenMap.put(email, new TokenDetails(token, expirationTime));
    }

    // Retrieve the token details
    public String get(String email) {
        return tokenMap.get(email).getToken();
    }

    // Remove the token
    public void remove(String email) {
        tokenMap.remove(email);
    }

    // Check if the token exists and is not expired
    public boolean isValid(String email, String token) {
        TokenDetails tokenDetails = tokenMap.get(email);
        if (tokenDetails != null) {
            return token.equals(tokenDetails.getToken()) && !tokenDetails.isExpired();
        }
        return false;
    }
}


