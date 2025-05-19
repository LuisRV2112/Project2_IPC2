import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cita {
  id: number;
  employeeId: number;
  serviceId: number;
  startTime: string;
  endTime: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private interesesUrl = '/api/interests';
  private citasUrl = '/api/client/appointments';

  constructor(private http: HttpClient) {}

  agregarInteres(interest: string): Observable<any> {
    const body = new URLSearchParams();
    body.set('interest', interest);
    return this.http.post(this.interesesUrl, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });
  }

  obtenerIntereses(): Observable<string[]> {
    return this.http.get<string[]>(this.interesesUrl);
  }

  eliminarInteres(interest: string): Observable<any> {
    const body = new URLSearchParams();
    body.set('interest', interest);
    return this.http.request('DELETE', this.interesesUrl, {
      body: body.toString(),
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });
  }

  obtenerCitas(): Observable<Cita[]> {
    return this.http.get<Cita[]>(this.citasUrl);
  }

  obtenerCitasCliente(): Observable<Cita[]> {
    return this.http.get<Cita[]>(this.citasUrl);
  }

  cancelarCita(id: number): Observable<{ message: string; appointmentId: number }> {
    return this.http.patch<{ message: string; appointmentId: number }>(
      `${this.citasUrl}/${id}`, {}
    );
  }
}
