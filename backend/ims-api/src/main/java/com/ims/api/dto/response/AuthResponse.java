package com.ims.api.dto.response;

public record AuthResponse(String token, String tokenType, long expiresIn, String email) {
    public static AuthResponse of(String token, long expiresInMs, String email) {
        return new AuthResponse(token, "Bearer", expiresInMs / 1000, email);
    }
}
