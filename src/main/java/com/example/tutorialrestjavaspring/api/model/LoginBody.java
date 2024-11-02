package com.example.tutorialrestjavaspring.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginBody {

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String password;

    public @NotNull @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @NotBlank String username) {
        this.username = username;
    }

    public @NotNull @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotBlank String password) {
        this.password = password;
    }
}
