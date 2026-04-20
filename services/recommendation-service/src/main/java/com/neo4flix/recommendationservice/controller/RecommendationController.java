package com.neo4flix.recommendationservice.controller;

import com.neo4flix.recommendationservice.model.Movie;
import com.neo4flix.recommendationservice.service.RecommendationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<Movie>> getPersonalizedRecommendations(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getRecommendations(authentication.getName()));
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Movie>> getFriendRecommendations(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getFriendRecommendations(authentication.getName()));
    }

    @GetMapping("/shared-with-me")
    public ResponseEntity<List<Movie>> getSharedWithMe(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getSharedWithMe(authentication.getName()));
    }

    @PostMapping("/share")
    public ResponseEntity<String> shareMovie(Authentication authentication, @RequestBody ShareRequest request) {
        boolean result = recommendationService.shareMovie(authentication.getName(), request.getReceiverUsername(), request.getMovieId());
        if (result) return ResponseEntity.ok("Film partagé avec succès !");
        return ResponseEntity.badRequest().body("Erreur lors du partage.");
    }

    @Data
    public static class ShareRequest {
        private String receiverUsername;
        private Long movieId;
    }
}
