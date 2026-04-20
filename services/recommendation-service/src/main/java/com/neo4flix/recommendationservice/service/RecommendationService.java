package com.neo4flix.recommendationservice.service;

import com.neo4flix.recommendationservice.model.Movie;
import com.neo4flix.recommendationservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<Movie> getRecommendations(String username) {
        return recommendationRepository.getContentBasedRecommendations(username);
    }

    public List<Movie> getFriendRecommendations(String username) {
        return recommendationRepository.getFriendRecommendations(username);
    }

    public boolean shareMovie(String sender, String receiver, Long movieId) {
        return recommendationRepository.shareMovieWithFriend(sender, receiver, movieId);
    }

    public List<Movie> getSharedWithMe(String username) {
        return recommendationRepository.getSharedWithMe(username);
    }
}
