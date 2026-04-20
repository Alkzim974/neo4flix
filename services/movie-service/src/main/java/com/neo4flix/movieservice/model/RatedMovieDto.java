package com.neo4flix.movieservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatedMovieDto {
    private Long id;
    private String title;
    private String description;
    private String releaseDate;
    private String posterUrl;
    private double averageRating;
    private double userScore;
}
