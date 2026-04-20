package com.neo4flix.ratingservice.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Movie")
@Data
public class Movie {
    @Id @GeneratedValue
    private Long id;
    private String title;
}
