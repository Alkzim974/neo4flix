package com.neo4flix.ratingservice.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("User")
@Data
public class User {
    @Id
    private Long id;
    private String username;

    @Relationship(type = "RATED", direction = Relationship.Direction.OUTGOING)
    private List<Rating> ratings = new ArrayList<>();
}
