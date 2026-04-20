package com.neo4flix.movieservice.repository;

import com.neo4flix.movieservice.model.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends Neo4jRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);

    @Query("MATCH (m:Movie) WHERE toLower(m.title) CONTAINS toLower($title) RETURN m")
    List<Movie> searchByTitle(String title);

    @Query("MATCH (m:Movie)-[:IN_GENRE]->(g:Genre) WHERE toLower(g.name) CONTAINS toLower($genre) RETURN m")
    List<Movie> searchByGenre(String genre);

    @Query("MATCH (m:Movie) WHERE m.releaseDate CONTAINS $year RETURN m ORDER BY m.releaseDate DESC")
    List<Movie> searchByYear(String year);

    @Query("""
        MATCH (m:Movie)
        WHERE ($title IS NULL OR toLower(m.title) CONTAINS toLower($title))
          AND ($year IS NULL OR m.releaseDate CONTAINS $year)
        WITH m
        OPTIONAL MATCH (m)-[:IN_GENRE]->(g:Genre)
        WHERE ($genre IS NULL OR toLower(g.name) CONTAINS toLower($genre))
        WITH m, g
        WHERE ($genre IS NULL OR g IS NOT NULL)
        RETURN DISTINCT m
        """)
    List<Movie> searchMultiCriteria(String title, String genre, String year);

    @Query("MATCH (u:User {username: $username})-[r:RATED]->(m:Movie) RETURN m")
    List<Movie> getRatedMovies(String username);

    @Query("MATCH (u:User {username: $username})-[:WANTS_TO_WATCH]->(m:Movie) RETURN m")
    List<Movie> getWatchlist(String username);

    @Query("MATCH (u:User {username: $username}) " +
           "MATCH (m:Movie) WHERE id(m) = $movieId " +
           "MERGE (u)-[:WANTS_TO_WATCH]->(m)")
    void addToWatchlist(String username, Long movieId);

    @Query("MATCH (u:User {username: $username})-[r:WANTS_TO_WATCH]->(m:Movie) WHERE id(m) = $movieId DELETE r")
    void removeFromWatchlist(String username, Long movieId);
}
