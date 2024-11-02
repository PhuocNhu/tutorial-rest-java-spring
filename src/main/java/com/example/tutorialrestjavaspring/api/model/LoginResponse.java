package com.example.tutorialrestjavaspring.api.model;

public class LoginResponse {
    private String token;
    private boolean success;
    private String message;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
