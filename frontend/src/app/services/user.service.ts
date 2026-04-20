import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getFriends(): Observable<any> {
    return this.http.get(`${this.apiUrl}/friends`, { headers: this.getHeaders() });
  }

  addFriend(username: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/friends/${username}`, {}, { headers: this.getHeaders(), responseType: 'text' });
  }

  removeFriend(username: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/friends/${username}`, { headers: this.getHeaders(), responseType: 'text' });
  }
}
