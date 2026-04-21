package com.neo4flix.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "friends")
@ToString(exclude = "friends")
public class User {

    @Id @GeneratedValue
    private Long id;

    private String username;
    private String email;
    private String password;

    // Secret Google Authenticator
    private String mfaSecret;
    // Si la 2FA est activée par l'utilisateur
    private boolean mfaEnabled;

    @Relationship(type = "FRIENDS_WITH", direction = Relationship.Direction.OUTGOING)
    private Set<User> friends = new HashSet<>();

}
