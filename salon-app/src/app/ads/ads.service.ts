import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Ad {
  id?: number;
  type: 'text' | 'image' | 'video';
  contentUrl: string;
  startDate: string;
  endDate: string;
  category: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdsService {
  private baseUrl = '/api/ads';

  constructor(private http: HttpClient) {}

  obtenerAnuncios(): Observable<{ data: Ad[] }> {
    return this.http.get<{ data: Ad[] }>(this.baseUrl);
  }

  crearAnuncio(ad: Ad): Observable<{ data: Ad }> {
    return this.http.post<{ data: Ad }>(this.baseUrl, ad);
  }

  actualizarAnuncio(id: number, ad: Ad): Observable<{ data: Ad }> {
    return this.http.put<{ data: Ad }>(`${this.baseUrl}/${id}`, ad);
  }

  eliminarAnuncio(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  registrarPago(adId: number, amount: number): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/payments/${adId}`, { amount });
  }

  republicarAnuncio(adId: number, nuevaInicio: string, nuevaFin: string): Observable<{ data: Ad }> {
    return this.http.put<{ data: Ad }>(`${this.baseUrl}/${adId}`, {
      startDate: nuevaInicio,
      endDate: nuevaFin
    });
  }
}
