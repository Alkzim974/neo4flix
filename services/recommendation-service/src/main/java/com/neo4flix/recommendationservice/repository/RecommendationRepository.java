package com.neo4flix.recommendationservice.repository;

import com.neo4flix.recommendationservice.model.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends Neo4jRepository<Movie, Long> {

    // Algorithme Content-Based (Basé sur les genres des films que l'utilisateur a bien notés)
    @Query("MATCH (u:User {username: $username})-[r:RATED]->(m:Movie)-[:IN_GENRE]->(g:Genre) " +
           "WHERE r.score >= 3.5 " +
           "WITH u, g, count(g) as genreFreq " +
           "MATCH (g)<-[:IN_GENRE]-(rec:Movie) " +
           "WHERE NOT EXISTS((u)-[:RATED]->(rec)) " +
           "RETURN rec " +
           "ORDER BY genreFreq DESC LIMIT 10")
    List<Movie> getContentBasedRecommendations(String username);

    // Recommandations Sociales : les films bien notés par les amis non encore vus
    @Query("MATCH (u:User {username: $username})-[:FRIENDS_WITH]->(friend:User) " +
           "MATCH (friend)-[r:RATED]->(m:Movie) " +
           "WHERE r.score >= 4.0 AND NOT EXISTS((u)-[:RATED]->(m)) " +
           "RETURN DISTINCT m " +
           "ORDER BY r.score DESC LIMIT 10")
    List<Movie> getFriendRecommendations(String username);

    // Partage d'un film : crée la relation RECOMMENDED entre deux utilisateurs
    @Query("MATCH (sender:User {username: $senderUsername}) " +
           "MATCH (receiver:User {username: $receiverUsername}) " +
           "MATCH (m:Movie) WHERE id(m) = $movieId " +
           "MERGE (sender)-[r:RECOMMENDED {movie: m.title}]->(receiver) " +
           "RETURN count(r) > 0")
    boolean shareMovieWithFriend(String senderUsername, String receiverUsername, Long movieId);

    // Films reçus en recommandation par les amis
    @Query("MATCH (friend:User)-[r:RECOMMENDED]->(u:User {username: $username}) " +
           "MATCH (m:Movie {title: r.movie}) " +
           "RETURN DISTINCT m")
    List<Movie> getSharedWithMe(String username);
}
