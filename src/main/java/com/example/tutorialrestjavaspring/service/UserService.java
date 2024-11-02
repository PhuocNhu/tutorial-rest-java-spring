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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private LocalUserDAO localUserDAO;
    private VerificationTokenDAO verificationTokenDAO;

    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;

    public UserService(LocalUserDAO localUserDAO, VerificationTokenDAO verificationTokenDAO, EncryptionService encryptionService, JWTService jwtService, EmailService emailService) {
        this.localUserDAO = localUserDAO;
        this.verificationTokenDAO = verificationTokenDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        if(localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
            localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setUsername(registrationBody.getUsername());
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        return localUserDAO.save(user);
    }

    private VerificationToken createVerificationToken(LocalUser user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.createVerificationToken(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser(LoginBody loginBody) throws EmailFailureException, UserNotVerifiedException {
        if(localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername()).isPresent()){
            LocalUser user = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername()).get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                if(user.getIsVerified()) {
                    return jwtService.createToken(user);
                }
                else {
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenDAO.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();
            if(!user.getIsVerified()){
                user.setIsVerified(true);
                localUserDAO.save(user);
                verificationTokenDAO.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        Optional<LocalUser> user = localUserDAO.findByEmailIgnoreCase(email);
        if(user.isPresent()){
            LocalUser localUser = user.get();
            String token = jwtService.createResetPasswordToken(localUser);
            emailService.sendPasswordResetEmail(localUser,token);
        } else {
            throw new EmailNotFoundException();
        }
    }

    public void resetPassword(PasswordResetBody body){
        String email = jwtService.getEmailFromResetPasswordToken(body.getToken());
        Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser localUser = opUser.get();
            localUser.setPassword(encryptionService.encryptPassword(body.getPassword()));
            localUserDAO.save(localUser);
        }
    }

    public boolean userHasPermissionToUser(LocalUser user, Long id){
        return user.getId().equals(id);
    }
}
