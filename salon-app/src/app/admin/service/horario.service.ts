import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface DiaHorario {
  dayOfWeek: number;
  openingTime: string;
  closingTime: string;
}

@Injectable({
  providedIn: 'root'
})
export class HorarioService {
  private apiUrl = 'api/admin/schedule';

  constructor(private http: HttpClient) {}

  obtenerHorarioSalon(): Observable<DiaHorario[]> {
    return this.http.get<DiaHorario[]>(this.apiUrl);
  }

  actualizarHorario(dia: DiaHorario): Observable<any> {
    console.log('Actualizando horario:', dia);
    return this.http.put(this.apiUrl, dia);
  }

  obtenerHorarioPorDia(dayOfWeek: number): Observable<DiaHorario> {
    return this.http.get<DiaHorario>(`${this.apiUrl}/${dayOfWeek}`);
  }
}