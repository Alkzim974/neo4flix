import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MovieService } from '../../services/movie.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  movies: any[] = [];
  searchTitle: string = '';
  searchGenre: string = '';
  searchYear: string = '';
  searchLabel: string = '';
  isLoggedIn = false;

  constructor(private movieService: MovieService, private authService: AuthService) {}

  ngOnInit() {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      if (status) {
        this.loadAllMovies();
      }
    });
  }

  loadAllMovies() {
    this.searchLabel = '';
    this.movieService.getMovies().subscribe({
      next: (data) => this.movies = data,
      error: (err) => console.error(err)
    });
  }

  onAdvancedSearch() {
    const hasTitle = this.searchTitle.trim().length > 0;
    const hasGenre = this.searchGenre.trim().length > 0;
    const hasYear = this.searchYear.trim().length > 0;

    if (!hasTitle && !hasGenre && !hasYear) {
      this.loadAllMovies();
      return;
    }

    // Construire le label descriptif
    const parts = [];
    if (hasTitle) parts.push(`titre "${this.searchTitle}"`);
    if (hasGenre) parts.push(`genre "${this.searchGenre}"`);
    if (hasYear) parts.push(`année ${this.searchYear}`);
    this.searchLabel = `Résultats pour : ${parts.join(', ')}`;

    this.movieService.searchAdvanced(
      hasTitle ? this.searchTitle : '',
      hasGenre ? this.searchGenre : '',
      hasYear ? this.searchYear : ''
    ).subscribe({
      next: (data) => this.movies = data,
      error: (err) => console.error(err)
    });
  }

  resetSearch() {
    this.searchTitle = '';
    this.searchGenre = '';
    this.searchYear = '';
    this.loadAllMovies();
  }
}
