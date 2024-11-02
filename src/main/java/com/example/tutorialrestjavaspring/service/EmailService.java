package com.example.tutorialrestjavaspring.service;

import com.example.tutorialrestjavaspring.exception.EmailFailureException;
import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private SimpleMailMessage makeMailMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        return message;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify to activate your account.");
        message.setText("Please click the link below to verify your account.\n" +
                frontendUrl + "/auth/verify?token=" + verificationToken.getToken());
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailFailureException();
        }
    }

    public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your password reset link.");
        message.setText("Please click the link below to reset your password.\n" + frontendUrl + "/auth/reset?token=" + token);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailFailureException();
        }
    }
}
