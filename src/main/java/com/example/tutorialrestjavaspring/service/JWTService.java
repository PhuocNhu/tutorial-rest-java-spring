package com.example.tutorialrestjavaspring.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tutorialrestjavaspring.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {
    @Value("${jwt.algorithm.key}")
    private String key;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "EMAIL";
    private static final String RESET_PASSWORD_KEY = "RESET_PASSWORD";

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(key);
    }

    public String createToken(LocalUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryInSeconds * 1000))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String createVerificationToken(LocalUser user) {
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryInSeconds * 1000))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String createResetPasswordToken(LocalUser user) {
        return JWT.create()
                .withClaim(RESET_PASSWORD_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 30 * 1000)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getEmailFromResetPasswordToken(String token) {
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(RESET_PASSWORD_KEY).asString();
    }

    public String getUserNameFromAuthenticationToken(String token) {
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(USERNAME_KEY).asString();
    }
}
