import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Employee {
  id: number;
  name: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class ServiceEmployeeService {
  private apiUrl = 'api/services/employees';

  constructor(private http: HttpClient) {}

  obtenerEmpleadosAsignados(serviceId: number): Observable<{ employees: Employee[] }> {
    return this.http.get<{ employees: Employee[] }>(`${this.apiUrl}/${serviceId}`);
  }

  asignarEmpleados(serviceId: number, employeeIds: number[]): Observable<any> {
    const params = new HttpParams({ fromObject: { employeeIds: employeeIds.map(id => id.toString()) } });
    return this.http.post(`${this.apiUrl}/${serviceId}`, params);
  }

  desasignarEmpleado(serviceId: number, employeeId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${serviceId}/employee/${employeeId}`);
  }
}
