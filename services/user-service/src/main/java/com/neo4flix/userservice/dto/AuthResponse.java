package com.neo4flix.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private boolean mfaRequired;
    private String message;
    private String qrCodeUrl; // Renvoyé uniquement lors de l'activation du 2FA
}
