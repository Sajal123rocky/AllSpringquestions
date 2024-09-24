package com.nw.model;

import java.time.LocalDateTime;

public class TokenDetails {

    private String token;
    private LocalDateTime expirationTime;

    public TokenDetails(String token, LocalDateTime expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
}
