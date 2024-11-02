package com.example.tutorialrestjavaspring.api.controller.auth;

import com.example.tutorialrestjavaspring.api.model.LoginBody;
import com.example.tutorialrestjavaspring.api.model.LoginResponse;
import com.example.tutorialrestjavaspring.api.model.PasswordResetBody;
import com.example.tutorialrestjavaspring.api.model.RegistrationBody;
import com.example.tutorialrestjavaspring.exception.EmailFailureException;
import com.example.tutorialrestjavaspring.exception.EmailNotFoundException;
import com.example.tutorialrestjavaspring.exception.UserAlreadyExistsException;
import com.example.tutorialrestjavaspring.exception.UserNotVerifiedException;
import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody){
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException exception){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = null;
        try {
            jwt = userService.loginUser(loginBody);
        } catch (UserNotVerifiedException e) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if(e.isNewEmailSent()){
                reason += "_NEW_EMAIL_SENT";
            }
            loginResponse.setMessage(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginResponse);
        } catch (EmailFailureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if(jwt != null){
            LoginResponse response = new LoginResponse();
            response.setToken(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
        if (userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/me")
    public LocalUser getUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email){
        try{
            userService.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body){
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }
}
