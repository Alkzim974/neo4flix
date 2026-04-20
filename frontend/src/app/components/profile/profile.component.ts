import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MovieService } from '../../services/movie.service';
import { RatingService } from '../../services/rating.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  activeTab = 'rated'; // 'rated', 'watchlist', 'friends'
  
  ratedMovies: any[] = [];
  watchlist: any[] = [];
  friends: any[] = [];
  
  newFriendUsername = '';
  friendMessage = '';

  constructor(
    private movieService: MovieService,
    private ratingService: RatingService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.movieService.getRatedMovies().subscribe(data => this.ratedMovies = data);
    this.movieService.getWatchlist().subscribe(data => this.watchlist = data);
    this.userService.getFriends().subscribe(data => this.friends = data);
  }

  removeRating(movieId: number) {
    this.ratingService.removeRating(movieId).subscribe(() => {
      this.loadData(); // Recharger les données
    });
  }

  removeFromWatchlist(movieId: number) {
    this.movieService.removeFromWatchlist(movieId).subscribe(() => {
      this.loadData();
    });
  }

  removeFriend(username: string) {
    this.userService.removeFriend(username).subscribe(() => {
      this.loadData();
    });
  }

  addFriend() {
    if (!this.newFriendUsername.trim()) return;
    this.userService.addFriend(this.newFriendUsername).subscribe({
      next: () => {
        this.friendMessage = "Ami ajouté avec succès !";
        this.newFriendUsername = '';
        this.loadData();
      },
      error: (err) => {
        this.friendMessage = err.error || "Utilisateur introuvable.";
      }
    });
  }
}
