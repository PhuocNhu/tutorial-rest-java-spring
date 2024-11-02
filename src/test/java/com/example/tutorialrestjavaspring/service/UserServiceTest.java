package com.example.tutorialrestjavaspring.service;

import com.example.tutorialrestjavaspring.api.model.LoginBody;
import com.example.tutorialrestjavaspring.api.model.PasswordResetBody;
import com.example.tutorialrestjavaspring.api.model.RegistrationBody;
import com.example.tutorialrestjavaspring.exception.EmailFailureException;
import com.example.tutorialrestjavaspring.exception.EmailNotFoundException;
import com.example.tutorialrestjavaspring.exception.UserAlreadyExistsException;
import com.example.tutorialrestjavaspring.exception.UserNotVerifiedException;
import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.VerificationToken;
import com.example.tutorialrestjavaspring.model.dao.LocalUserDAO;
import com.example.tutorialrestjavaspring.model.dao.VerificationTokenDAO;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot","secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDAO localUserDAO;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private VerificationTokenDAO verificationTokenDAO;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody registrationBody = new RegistrationBody();
        registrationBody.setUsername("UserA");
        registrationBody.setEmail("UserServiceTest$testRegisterUser@junit.com");
        registrationBody.setFirstName("FirstName");
        registrationBody.setLastName("LastName");
        registrationBody.setPassword("MySecretPassword123");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationBody), "Username should already exist");
        registrationBody.setUsername("UserServiceTest$testRegisterUser");
        registrationBody.setEmail("UserA@junit.com");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationBody), "Email should already exist");
        registrationBody.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(registrationBody),
                "User should register successfully");
        Assertions.assertEquals(registrationBody.getEmail(),
                greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("UserA-NotExists");
        loginBody.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(userService.loginUser(loginBody),"User should not exist");
        loginBody.setUsername("UserA");
        Assertions.assertNull(userService.loginUser(loginBody),"Password should be incorrect");
        loginBody.setPassword("PasswordA123");
        Assertions.assertNotNull(userService.loginUser(loginBody),"User should login successfully");
        loginBody.setUsername("UserB");
        loginBody.setPassword("PasswordB123");
        try{
            userService.loginUser(loginBody);
            Assertions.fail("User should not have email verified");
        } catch (UserNotVerifiedException e) {
            Assertions.assertTrue(e.isNewEmailSent(), "Verification should be sent");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);
        }
        try{
            userService.loginUser(loginBody);
            Assertions.fail("User should not have email verified");
        } catch (UserNotVerifiedException e) {
            Assertions.assertFalse(e.isNewEmailSent(), "Verification should not be resent");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);
        }
    }

    @Test
    @Transactional
    public void testVerifyUser() throws EmailFailureException {
        Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token is not exist and should return false");
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("UserB");
        loginBody.setPassword("PasswordB123");
        try {
            userService.loginUser(loginBody);
            Assertions.fail("User should not have email verified");
        } catch (UserNotVerifiedException e) {
            List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(userService.verifyUser(token), "Token should be verified");
            Assertions.assertNotNull(loginBody,"The user should now be verified");
        }
    }

    @Test
    @Transactional
    public void testForgotPassword() throws MessagingException {
        Assertions.assertThrows(EmailNotFoundException.class, () -> userService.forgotPassword("UserNotExist@junit.com"));
        Assertions.assertDoesNotThrow(() -> userService.forgotPassword("UserA@junit.com"));
        Assertions.assertEquals("UserA@junit.com",
                greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void testResetPassword() {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.createResetPasswordToken(user);
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("Password123456");
        userService.resetPassword(body);
        user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        Assertions.assertTrue(encryptionService.verifyPassword("Password123456",user.getPassword()),
                "Password change should have been written to DB");
    }
}
