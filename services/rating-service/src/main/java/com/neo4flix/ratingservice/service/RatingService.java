package com.neo4flix.ratingservice.service;

import com.neo4flix.ratingservice.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public void rateMovie(String username, Long movieId, double score) {
        if (score < 0 || score > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 5");
        }
        ratingRepository.saveRatingDirectly(username, movieId, score);
    }

    public void removeRating(String username, Long movieId) {
        ratingRepository.deleteRatingDirectly(username, movieId);
    }
}
