import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RatingService {
  private apiUrl = '/api/ratings';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  rateMovie(movieId: number, score: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/movie/${movieId}`, { score }, { headers: this.getHeaders(), responseType: 'text' });
  }

  removeRating(movieId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/movie/${movieId}`, { headers: this.getHeaders(), responseType: 'text' });
  }
}
