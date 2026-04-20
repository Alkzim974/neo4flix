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

  constructor(private recommendationService: RecommendationService) {}

  ngOnInit() {
    this.setTab('personal');
  }

  setTab(tab: string) {
    this.activeTab = tab;
    this.movies = [];

    if (tab === 'personal') {
      this.recommendationService.getRecommendations().subscribe({
        next: (data) => this.movies = data,
        error: (err) => console.error(err)
      });
    } else if (tab === 'friends') {
      this.recommendationService.getFriendRecommendations().subscribe({
        next: (data) => this.movies = data,
        error: (err) => console.error(err)
      });
    } else if (tab === 'shared') {
      this.recommendationService.getSharedWithMe().subscribe({
        next: (data) => this.movies = data,
        error: (err) => console.error(err)
      });
    }
  }
}
