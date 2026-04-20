package com.neo4flix.movieservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Movie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String releaseDate; // Ou LocalDate
    private String posterUrl;
    
    // Moyenne des notes calculée (pour éviter des recalculs constants)
    @Builder.Default
    private double averageRating = 0.0;

    @Relationship(type = "IN_GENRE", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();
}
