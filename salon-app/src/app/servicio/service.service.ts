import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, Observable } from 'rxjs';

export interface Servicio {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  durationMin: number;
  price: number;
  isActive: boolean;
  catalogoPdfUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class ServiceService {
  private apiUrl = '/api/services';

  constructor(private http: HttpClient) {}

  obtenerServicios() {
    return this.http.get<{ data: Servicio[] }>(this.apiUrl).pipe(
      map(response => response.data)
    );
    
  }

  crearServicio(servicio: Partial<Servicio>): Observable<any> {
    return this.http.post(this.apiUrl, servicio);
  }
  
  actualizarServicio(id: number, servicio: Partial<Servicio>): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, servicio);
  }
  
  obtenerServicio(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  subirCatalogo(serviceId: number, archivo: File): Observable<any> {
    const formData = new FormData();
    formData.append('catalog', archivo);
  
    return this.http.post(`${this.apiUrl}/catalog/${serviceId}`, formData);
  }
  
  obtenerCatalogoUrl(serviceId: number): string {
    return `${this.apiUrl}/catalog/${serviceId}`;
  }
  
  cambiarEstadoServicio(id: number, active: boolean): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}`, { active });
  }

  descargarCatalogo(serviceId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/catalog/${serviceId}`, {
      responseType: 'blob' })
  }

  descargarReporte(tipo: 'top-most' | 'top-least' | 'top-income') {
    console.log('Descargando reporte de tipo:', tipo);
    return this.http.get(`${this.apiUrl}/reports/${tipo}`, {
      responseType: 'blob'
    });
  }
}
