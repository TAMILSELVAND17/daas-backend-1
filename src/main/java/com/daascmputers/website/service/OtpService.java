package com.daascmputers.website.service;


import java.util.concurrent.CompletableFuture;

public interface OtpService {
    CompletableFuture<Void> sendOtp(String input);
    boolean verifyOtp(String input, String otp);
}

