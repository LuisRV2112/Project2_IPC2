import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

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

  constructor(private http: HttpClient) {}

  testBackend(): Observable<any> {
    return this.http.get('/api/invoices');
  }

  register(data: RegisterPayload): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  login(data: LoginPayload): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(`${this.apiUrl}/login`, data, { headers }).pipe(
      tap((response: any) => {
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('user_role', response.role); // Asumiendo que el backend devuelve el rol
      })
    );
  }
  
}
