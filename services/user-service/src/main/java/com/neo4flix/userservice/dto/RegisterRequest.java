package com.neo4flix.userservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private boolean enableMfa; // L'utilisateur coche la case pour activer ou non
}
