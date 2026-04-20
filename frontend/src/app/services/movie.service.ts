import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class MovieService {
  private apiUrl = '/api/movies';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getMovies(): Observable<any> {
    return this.http.get(this.apiUrl, { headers: this.getHeaders() });
  }

  getMovieById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  searchMovies(query: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/search?q=${query}`, { headers: this.getHeaders() });
  }

  searchAdvanced(title: string, genre: string, year: string): Observable<any> {
    const params = new URLSearchParams();
    if (title) params.append('title', title);
    if (genre) params.append('genre', genre);
    if (year) params.append('year', year);
    return this.http.get(`${this.apiUrl}/search/advanced?${params.toString()}`, { headers: this.getHeaders() });
  }

  getRatedMovies(): Observable<any> {
    return this.http.get(`${this.apiUrl}/rated`, { headers: this.getHeaders() });
  }

  getWatchlist(): Observable<any> {
    return this.http.get(`${this.apiUrl}/watchlist`, { headers: this.getHeaders() });
  }

  addToWatchlist(movieId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/watchlist/${movieId}`, {}, { headers: this.getHeaders(), responseType: 'text' });
  }

  removeFromWatchlist(movieId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/watchlist/${movieId}`, { headers: this.getHeaders(), responseType: 'text' });
  }
}
