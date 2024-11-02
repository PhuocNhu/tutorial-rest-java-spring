package com.example.tutorialrestjavaspring.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetBody {

    @NotBlank
    @NotNull
    private String token;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    private String password;

    public @NotBlank @NotNull String getToken() {
        return token;
    }

    public void setToken(@NotBlank @NotNull String token) {
        this.token = token;
    }

    public @NotNull @NotBlank @Size(min = 6, max = 32) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotBlank @Size(min = 6, max = 32) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$") String password) {
        this.password = password;
    }
}
