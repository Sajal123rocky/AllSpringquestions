package com.nw.model;

import java.time.LocalDateTime;

public class OtpDetails {
    private String otp;
    private LocalDateTime timestamp;

    public OtpDetails(String otp, LocalDateTime timestamp) {
        this.otp = otp;
        this.timestamp = timestamp;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

