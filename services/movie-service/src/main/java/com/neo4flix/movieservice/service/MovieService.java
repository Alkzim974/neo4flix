package com.neo4flix.movieservice.service;

import com.neo4flix.movieservice.model.Movie;
import com.neo4flix.movieservice.model.RatedMovieDto;
import com.neo4flix.movieservice.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final Neo4jClient neo4jClient;

    /**
     * Retourne tous les films avec la note optionnelle de l'utilisateur.
     */
    public List<RatedMovieDto> getAllMovies(String username) {
        String query = "MATCH (m:Movie) " +
                       "OPTIONAL MATCH (u:User {username: $username})-[r:RATED]->(m) " +
                       "RETURN id(m) AS id, m.title AS title, m.description AS description, " +
                       "m.releaseDate AS releaseDate, m.posterUrl AS posterUrl, " +
                       "m.averageRating AS averageRating, r.score AS userScore";
        
        return executeQuery(query, username, null, null, null);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film introuvable"));
    }

    public List<RatedMovieDto> searchMovies(String titleQuery, String username) {
        String query = "MATCH (m:Movie) WHERE toLower(m.title) CONTAINS toLower($title) " +
                       "OPTIONAL MATCH (u:User {username: $username})-[r:RATED]->(m) " +
                       "RETURN id(m) AS id, m.title AS title, m.description AS description, " +
                       "m.releaseDate AS releaseDate, m.posterUrl AS posterUrl, " +
                       "m.averageRating AS averageRating, r.score AS userScore";
        
        return executeQuery(query, username, titleQuery, null, null);
    }

    public List<RatedMovieDto> getMoviesByGenre(String genre, String username) {
        String query = "MATCH (m:Movie)-[:IN_GENRE]->(g:Genre) WHERE toLower(g.name) CONTAINS toLower($genre) " +
                       "OPTIONAL MATCH (u:User {username: $username})-[r:RATED]->(m) " +
                       "RETURN id(m) AS id, m.title AS title, m.description AS description, " +
                       "m.releaseDate AS releaseDate, m.posterUrl AS posterUrl, " +
                       "m.averageRating AS averageRating, r.score AS userScore";
        
        return executeQuery(query, username, null, genre, null);
    }

    public List<RatedMovieDto> searchByYear(String year, String username) {
        String query = "MATCH (m:Movie) WHERE m.releaseDate CONTAINS $year " +
                       "OPTIONAL MATCH (u:User {username: $username})-[r:RATED]->(m) " +
                       "RETURN id(m) AS id, m.title AS title, m.description AS description, " +
                       "m.releaseDate AS releaseDate, m.posterUrl AS posterUrl, " +
                       "m.averageRating AS averageRating, r.score AS userScore " +
                       "ORDER BY m.releaseDate DESC";
        
        return executeQuery(query, username, null, null, year);
    }

    public List<RatedMovieDto> searchMultiCriteria(String title, String genre, String year, String username) {
        String t = (title != null && !title.isBlank()) ? title : null;
        String g = (genre != null && !genre.isBlank()) ? genre : null;
        String y = (year != null && !year.isBlank()) ? year : null;

        String query = "MATCH (m:Movie) " +
                       "WHERE ($title IS NULL OR toLower(m.title) CONTAINS toLower($title)) " +
                       "  AND ($year IS NULL OR m.releaseDate CONTAINS $year) " +
                       "WITH m " +
                       "OPTIONAL MATCH (m)-[:IN_GENRE]->(gn:Genre) " +
                       "WHERE ($genre IS NULL OR toLower(gn.name) CONTAINS toLower($genre)) " +
                       "WITH m, gn " +
                       "WHERE ($genre IS NULL OR gn IS NOT NULL) " +
                       "WITH DISTINCT m " +
                       "OPTIONAL MATCH (u:User {username: $username})-[r:RATED]->(m) " +
                       "RETURN id(m) AS id, m.title AS title, m.description AS description, " +
                       "m.releaseDate AS releaseDate, m.posterUrl AS posterUrl, " +
                       "m.averageRating AS averageRating, r.score AS userScore";

        return executeQuery(query, username, t, g, y);
    }

    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<RatedMovieDto> getRatedMovies(String username) {
        return getAllMovies(username).stream()
                .filter(m -> m.getUserScore() > 0)
                .collect(Collectors.toList());
    }

    public List<Movie> getWatchlist(String username) {
        return movieRepository.getWatchlist(username);
    }

    public void addToWatchlist(String username, Long movieId) {
        movieRepository.addToWatchlist(username, movieId);
    }

    public void removeFromWatchlist(String username, Long movieId) {
        movieRepository.removeFromWatchlist(username, movieId);
    }

    // Méthode helper pour exécuter les requêtes de recherche avec Neo4jClient
    // IMPORTANT: tous les paramètres sont toujours bindés (même null), car Cypher
    // a besoin que "$param IS NULL" soit évaluable même si la valeur est absente.
    private List<RatedMovieDto> executeQuery(String cypher, String username, String title, String genre, String year) {
        return neo4jClient.query(cypher)
                .bind(username != null ? username : "").to("username")
                .bind(title).to("title")
                .bind(genre).to("genre")
                .bind(year).to("year")
                .fetchAs(RatedMovieDto.class)
                .mappedBy((typeSystem, record) -> new RatedMovieDto(
                        record.get("id").asLong(0),
                        record.get("title").asString(null),
                        record.get("description").asString(null),
                        record.get("releaseDate").asString(null),
                        record.get("posterUrl").asString(null),
                        record.get("averageRating").asDouble(0.0),
                        record.get("userScore").asDouble(0.0)
                ))
                .all().stream().collect(Collectors.toList());
    }
}
