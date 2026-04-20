package com.neo4flix.userservice.service;

import com.neo4flix.userservice.dto.AuthRequest;
import com.neo4flix.userservice.dto.AuthResponse;
import com.neo4flix.userservice.dto.RegisterRequest;
import com.neo4flix.userservice.model.User;
import com.neo4flix.userservice.repository.UserRepository;
import com.neo4flix.userservice.security.CustomUserDetails;
import com.neo4flix.userservice.security.JwtUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Nom d'utilisateur déjà pris");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà pris");
        }

        // Politique de mot de passe fort (Audit require strong passwords)
        if (request.getPassword().length() < 8) {
            throw new RuntimeException("Mot de passe trop faible (8 caractères minimum)");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mfaEnabled(request.isEnableMfa())
                .build();

        String qrCodeUrl = null;
        if (request.isEnableMfa()) {
            GoogleAuthenticatorKey key = gAuth.createCredentials();
            user.setMfaSecret(key.getKey());
            // Format du QR Code OTPAuth
            qrCodeUrl = "otpauth://totp/Neo4flix:" + user.getUsername() + "?secret=" + key.getKey() + "&issuer=Neo4flix";
        }

        userRepository.save(user);

        return AuthResponse.builder()
                .message("Inscription réussie")
                .qrCodeUrl(qrCodeUrl)
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        // Authentification classique
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérification 2FA si activé
        if (user.isMfaEnabled()) {
            if (request.getMfaCode() <= 0) {
                return AuthResponse.builder()
                        .mfaRequired(true)
                        .message("Code 2FA requis")
                        .build();
            }
            boolean isCodeValid = gAuth.authorize(user.getMfaSecret(), request.getMfaCode());
            if (!isCodeValid) {
                throw new RuntimeException("Code 2FA invalide");
            }
        }

        // Génération du JWT
        String token = jwtUtil.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .mfaRequired(false)
                .message("Connexion réussie")
                .build();
    }
}
