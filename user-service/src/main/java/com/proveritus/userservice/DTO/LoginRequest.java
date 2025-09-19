package com.proveritus.userservice.DTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;

    public String getUsername() {
        return usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }
}