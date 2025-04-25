import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CitaEmpleado {
  id: number;
  clientId: number;
  startTime: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmpleadoService {
  private apiUrl = '/api/employee/appointments';
  constructor(private http: HttpClient) {}

  obtenerCitas(date: string): Observable<CitaEmpleado[]> {
    const params = new HttpParams().set('date', date);
    return this.http.get<CitaEmpleado[]>(`${this.apiUrl}`, { params });
  }

  actualizarEstadoCita(appointmentId: number, status: string, noShow: boolean = false): Observable<any> {
    console.log('Actualizando estado de cita:', appointmentId, status, noShow);
    return this.http.put(`${this.apiUrl}/${appointmentId}`, {
      status,
      noShow
    });
  }

  obtenerFactura(invoiceId: number): Observable<Blob> {
    return this.http.get(`/api/invoices/${invoiceId}`, { responseType: 'blob' });
  }

  obtenerPerfil(): Observable<any> {
    return this.http.get('/api/profile');
  }
  
  actualizarPerfil(perfil: any): Observable<any> {
    return this.http.post('/api/profile', perfil);
  }
  
}