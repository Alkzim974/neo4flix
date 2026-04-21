package com.neo4flix.userservice.repository;

import com.neo4flix.userservice.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Créer la relation FRIENDS_WITH directement en Cypher (MERGE = pas de doublon)
    @Query("MATCH (u:User {username: $username}), (f:User {username: $friendUsername}) " +
           "MERGE (u)-[:FRIENDS_WITH]->(f)")
    void createFriendship(String username, String friendUsername);

    // Récupérer les amis directement en Cypher -> retourne des nœuds User
    @Query("MATCH (u:User {username: $username})-[:FRIENDS_WITH]->(f:User) RETURN f")
    List<User> findFriendsByUsername(String username);

    // Supprimer la relation FRIENDS_WITH directement en Cypher
    @Query("MATCH (u:User {username: $username})-[r:FRIENDS_WITH]->(f:User {username: $friendUsername}) DELETE r")
    void deleteFriendship(String username, String friendUsername);
}
