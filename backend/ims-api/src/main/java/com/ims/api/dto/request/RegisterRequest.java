package com.ims.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
    @NotBlank @Size(min = 6, max = 6, message = "Registration code must be exactly 6 characters") String registrationCode
) {}
