package com.neo4flix.userservice.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private int mfaCode; // Le code 2FA envoyé lors du login (0 s'il n'y en a pas encore)
}
