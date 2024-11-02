package com.example.tutorialrestjavaspring.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.dao.LocalUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDAO localUserDAO;

    @Value("${jwt.algorithm.key}")
    private String key;

    @Test
    public void testVerificationTokenNotUsableForLogin(){
        LocalUser localUser = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.createVerificationToken(localUser);
        Assertions.assertNull(jwtService.getUserNameFromAuthenticationToken(token), "Verification token should not contain username");
    }

    @Test
    public void testAuthTokenUsableForLogin(){
        LocalUser localUser = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.createToken(localUser);
        Assertions.assertEquals(localUser.getUsername(), jwtService.getUserNameFromAuthenticationToken(token), "Authentication token should contain username");
    }

    @Test
    public void testAuthTokenNotGeneratedByTrueAlgorithm(){
        String token = JWT.create().withClaim("USERNAME","UserA").sign(Algorithm.HMAC256("NotTheSecretKey"));
        Assertions.assertThrows(SignatureVerificationException.class, () -> jwtService.getUserNameFromAuthenticationToken(token));
    }

    @Test
    public void testAuthTokenCorrectlySigned(){
        String token = JWT.create().withClaim("USERNAME","UserA").sign(Algorithm.HMAC256(key));
        Assertions.assertThrows(MissingClaimException.class, () -> jwtService.getUserNameFromAuthenticationToken(token));
    }

    @Test
    public void testResetPasswordTokenNotGeneratedByTrueAlgorithm(){
        String token = JWT.create().withClaim("RESET_PASSWORD","UserA@junit.com").sign(Algorithm.HMAC256("NotTheSecretKey"));
        Assertions.assertThrows(SignatureVerificationException.class, () -> jwtService.getEmailFromResetPasswordToken(token));
    }

    @Test
    public void testResetPasswordTokenCorrectlySigned(){
        String token = JWT.create().withClaim("RESET_PASSWORD","UserA@junit.com").sign(Algorithm.HMAC256(key));
        Assertions.assertThrows(MissingClaimException.class, () -> jwtService.getEmailFromResetPasswordToken(token));
    }

    @Test
    public void testPasswordResetToken(){
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.createResetPasswordToken(user);
        Assertions.assertEquals(user.getEmail(), jwtService.getEmailFromResetPasswordToken(token), "Authentication token should contain username");
    }
}
