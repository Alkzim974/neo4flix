import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MovieService } from '../../services/movie.service';
import { RatingService } from '../../services/rating.service';
import { RecommendationService } from '../../services/recommendation.service';

@Component({
  selector: 'app-movie-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './movie-detail.component.html',
  styleUrl: './movie-detail.component.css'
})
export class MovieDetailComponent implements OnInit {
  movie: any = null;
  userRating: number = 2.5; // Default value
  ratingMessage: string = '';
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private movieService: MovieService,
    private ratingService: RatingService,
    private recommendationService: RecommendationService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.loadMovie(Number(id));
      }
    });
  }

  loadMovie(id: number) {
    this.movieService.getMovieById(id).subscribe({
      next: (data) => this.movie = data,
      error: (err) => this.error = "Erreur lors du chargement des détails."
    });
  }

  submitRating() {
    if (!this.movie) return;
    
    this.ratingService.rateMovie(this.movie.id, this.userRating).subscribe({
      next: (res) => {
        this.ratingMessage = "Votre note a été enregistrée avec succès ! Neo4j va l'utiliser pour vos futures recommandations.";
      },
      error: (err) => {
        this.ratingMessage = "Erreur lors de l'enregistrement de la note.";
      }
    });
  }

  watchlistMsg = '';
  toggleWatchlist() {
    if (!this.movie) return;
    this.movieService.addToWatchlist(this.movie.id).subscribe({
      next: () => this.watchlistMsg = "Ajouté avec succès ! (Si déjà présent, pas de doublon car Neo4j MERGE la relation)",
      error: () => this.watchlistMsg = "Erreur."
    });
  }

  shareUsername = '';
  shareMsg = '';
  shareWithFriend() {
    if (!this.movie || !this.shareUsername.trim()) return;
    this.recommendationService.shareMovie(this.shareUsername.trim(), this.movie.id).subscribe({
      next: () => {
        this.shareMsg = `Film partagé avec succès avec ${this.shareUsername} !`;
        this.shareUsername = '';
      },
      error: () => this.shareMsg = "Erreur : utilisateur introuvable ou partage échoué."
    });
  }
}
