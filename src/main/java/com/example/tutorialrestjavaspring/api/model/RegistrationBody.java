package com.example.tutorialrestjavaspring.api.model;

import jakarta.validation.constraints.*;

public class RegistrationBody {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    private String username;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    private String password;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    public @NotNull @NotBlank @Size(min = 3, max = 255) String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @NotBlank @Size(min = 3, max = 255) String username) {
        this.username = username;
    }

    public @NotNull @NotBlank @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @NotBlank @Email String email) {
        this.email = email;
    }

    public @NotNull @NotBlank @Size(min = 6, max = 32) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotBlank @Size(min = 6, max = 32) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$") String password) {
        this.password = password;
    }

    public @NotNull @NotBlank String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull @NotBlank String firstName) {
        this.firstName = firstName;
    }

    public @NotNull @NotBlank String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull @NotBlank String lastName) {
        this.lastName = lastName;
    }
}
