package com.neo4flix.userservice.controller;

import com.neo4flix.userservice.dto.UserSummaryDto;
import com.neo4flix.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDto> getCurrentProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getProfileSummary(username));
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/friends/{username}")
    public ResponseEntity<String> addFriend(Authentication authentication, @PathVariable("username") String friendUsername) {
        String currentUsername = authentication.getName();
        try {
            userService.addFriend(currentUsername, friendUsername);
            return ResponseEntity.ok("Ami ajouté avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserSummaryDto>> getMyFriends(Authentication authentication) {
        return ResponseEntity.ok(userService.getFriends(authentication.getName()));
    }

    @DeleteMapping("/friends/{username}")
    public ResponseEntity<String> removeFriend(Authentication authentication, @PathVariable("username") String friendUsername) {
        try {
            userService.removeFriend(authentication.getName(), friendUsername);
            return ResponseEntity.ok("Ami retiré de votre liste");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
