package com.daascmputers.website.service;

public interface JwtService {
    String generateToken(String subject,long userId);
    String validateToken(String token);
}