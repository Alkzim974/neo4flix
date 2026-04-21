import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RecommendationService } from '../../services/recommendation.service';

@Component({
  selector: 'app-recommendations',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './recommendations.component.html',
  styleUrl: './recommendations.component.css'
})
export class RecommendationsComponent implements OnInit {
  movies: any[] = [];
  activeTab = 'personal';
  errorMessage = '';

  constructor(private recommendationService: RecommendationService) {}

  ngOnInit() {
    this.setTab('personal');
  }

  setTab(tab: string) {
    this.activeTab = tab;
    this.movies = [];
    this.errorMessage = '';

    if (tab === 'personal') {
      this.recommendationService.getRecommendations().subscribe({
        next: (data) => this.movies = data,
        error: (err) => this.errorMessage = 'Erreur lors du chargement des recommandations.'
      });
    } else if (tab === 'friends') {
      this.recommendationService.getFriendRecommendations().subscribe({
        next: (data) => this.movies = data,
        error: (err) => this.errorMessage = 'Erreur API : ' + (err.error || err.message)
      });
    } else if (tab === 'shared') {
      this.recommendationService.getSharedWithMe().subscribe({
        next: (data) => this.movies = data,
        error: (err) => this.errorMessage = 'Erreur lors du chargement des films partagés.'
      });
    }
  }
}
