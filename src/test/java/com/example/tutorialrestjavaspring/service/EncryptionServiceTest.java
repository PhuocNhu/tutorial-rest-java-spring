package com.example.tutorialrestjavaspring.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void testEncryptionService() {
        String password = "PasswordASecret!123";
        String hash = encryptionService.encryptPassword(password);
        Assertions.assertTrue(encryptionService.verifyPassword(password, hash), "Password should be match");
        Assertions.assertFalse(encryptionService.verifyPassword(password + "Wrong!", hash), "Password should not be match");
    }
}
