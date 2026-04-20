package com.neo4flix.ratingservice.controller;

import com.neo4flix.ratingservice.service.RatingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/movie/{movieId}")
    public ResponseEntity<String> rateMovie(Authentication authentication, 
                                            @PathVariable Long movieId, 
                                            @RequestBody RatingRequest request) {
        String username = authentication.getName();
        try {
            ratingService.rateMovie(username, movieId, request.getScore());
            return ResponseEntity.ok("Film noté avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Data
    public static class RatingRequest {
        private double score;
    }

    @DeleteMapping("/movie/{movieId}")
    public ResponseEntity<String> removeRating(Authentication authentication, @PathVariable Long movieId) {
        try {
            ratingService.removeRating(authentication.getName(), movieId);
            return ResponseEntity.ok("Note supprimée.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
