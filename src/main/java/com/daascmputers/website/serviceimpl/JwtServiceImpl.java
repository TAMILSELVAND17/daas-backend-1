package com.daascmputers.website.serviceimpl;


import com.daascmputers.website.exceptionhandler.Exceptions;
import com.daascmputers.website.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtServiceImpl implements JwtService {
    private final String SECRET = "secret_key";

    @Override
    public String generateToken(String subject,long userId) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("userId", userId) // Add userId to payload
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    @Override
    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            throw new Exceptions("Token has expired");
        }
    }
}