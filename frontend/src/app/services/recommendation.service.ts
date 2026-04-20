import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {
  private apiUrl = '/api/recommendations';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  getRecommendations(): Observable<any> {
    return this.http.get(this.apiUrl, { headers: this.getHeaders() });
  }

  getFriendRecommendations(): Observable<any> {
    return this.http.get(`${this.apiUrl}/friends`, { headers: this.getHeaders() });
  }

  getSharedWithMe(): Observable<any> {
    return this.http.get(`${this.apiUrl}/shared-with-me`, { headers: this.getHeaders() });
  }

  shareMovie(receiverUsername: string, movieId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/share`,
      { receiverUsername, movieId },
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }
}
