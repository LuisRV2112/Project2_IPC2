import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

interface RegisterPayload {
  dpi: string;
  telefono: string;
  direccion: string;
  email: string;
  password: string;
}

interface LoginPayload {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.checkToken());
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient) {}

  testBackend(): Observable<any> {
    return this.http.get('/api/invoices');
  }

  private checkToken(): boolean {
    return !!localStorage.getItem('auth_token');
  }

  register(data: RegisterPayload): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  login(data: LoginPayload): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(`${this.apiUrl}/login`, data, { headers }).pipe(
      tap((response: any) => {
        console.log('Login response:', response);
        localStorage.setItem('user_id', response.user.id);
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('user_role', response.user.roleId);
        this.isLoggedInSubject.next(true);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    this.isLoggedInSubject.next(false);
  }
  
  getRole(): string | null {
    return localStorage.getItem('user_role');
  }

}
