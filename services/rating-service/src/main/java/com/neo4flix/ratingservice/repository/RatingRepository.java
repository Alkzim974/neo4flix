package com.neo4flix.ratingservice.repository;

import com.neo4flix.ratingservice.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends Neo4jRepository<User, Long> {
    
    Optional<User> findByUsername(String username);

    // Une requête Cypher dédiée est parfois plus performante pour insérer ou mettre à jour un rating directement.
    @Query("MATCH (u:User {username: $username}) " +
           "MATCH (m:Movie) WHERE id(m) = $movieId " +
           "MERGE (u)-[r:RATED]->(m) " +
           "SET r.score = $score " +
           "RETURN count(r) > 0")
    boolean saveRatingDirectly(String username, Long movieId, double score);

    @Query("MATCH (u:User {username: $username})-[r:RATED]->(m:Movie) WHERE id(m) = $movieId DELETE r")
    void deleteRatingDirectly(String username, Long movieId);
}
