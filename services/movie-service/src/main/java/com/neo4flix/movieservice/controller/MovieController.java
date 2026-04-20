package com.neo4flix.movieservice.controller;

import com.neo4flix.movieservice.model.Movie;
import com.neo4flix.movieservice.model.RatedMovieDto;
import com.neo4flix.movieservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<RatedMovieDto>> getAllMovies(Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(movieService.getAllMovies(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RatedMovieDto>> searchMovies(@RequestParam String q, Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(movieService.searchMovies(q, username));
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<List<RatedMovieDto>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String year,
            Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(movieService.searchMultiCriteria(title, genre, year, username));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<RatedMovieDto>> getMoviesByGenre(@PathVariable String genre, Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre, username));
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.addMovie(movie));
    }

    @GetMapping("/rated")
    public ResponseEntity<List<RatedMovieDto>> getRatedMovies(Authentication authentication) {
        return ResponseEntity.ok(movieService.getRatedMovies(authentication.getName()));
    }

    @GetMapping("/watchlist")
    public ResponseEntity<List<Movie>> getWatchlist(Authentication authentication) {
        return ResponseEntity.ok(movieService.getWatchlist(authentication.getName()));
    }

    @PostMapping("/watchlist/{movieId}")
    public ResponseEntity<String> addToWatchlist(Authentication authentication, @PathVariable Long movieId) {
        movieService.addToWatchlist(authentication.getName(), movieId);
        return ResponseEntity.ok("Film ajouté à la watchlist");
    }

    @DeleteMapping("/watchlist/{movieId}")
    public ResponseEntity<String> removeFromWatchlist(Authentication authentication, @PathVariable Long movieId) {
        movieService.removeFromWatchlist(authentication.getName(), movieId);
        return ResponseEntity.ok("Film retiré de la watchlist");
    }
}
