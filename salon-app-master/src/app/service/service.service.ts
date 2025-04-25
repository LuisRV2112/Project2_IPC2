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

  obtenerServicios(): Observable<Servicio[]> {
    const token = localStorage.getItem('auth_token') || '';
    console.log('Token:', token); // Verifica si el token se está obteniendo correctamente
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    //este get devuleve 
    /* {
    "data": [
        {
            "id": 1,
            "name": "Corte de Cabello",
            "description": "Corte de cabello para hombres y mujeres.",
            "imageUrl": "https://example.com/corte.jpg",
            "durationMin": 30,
            "price": 150.0,
            "isActive": true
        },
        {
            "id": 2,
            "name": "Manicura",
            "description": "Servicio de manicura con esmalte.",
            "imageUrl": "https://example.com/manicura.jpg",
            "durationMin": 45,
            "price": 200.0,
            "isActive": true
        },
        {
            "id": 3,
            "name": "Pedicura",
            "description": "Servicio de pedicura con esmalte.",
            "imageUrl": "https://example.com/pedicura.jpg",
            "durationMin": 45,
            "price": 200.0,
            "isActive": true
        },
        {
            "id": 4,
            "name": "Masaje Relajante",
            "description": "Masaje relajante de cuerpo completo.",
            "imageUrl": "https://example.com//masaje.jpg",
            "durationMin": 60,
            "price": 300.0,
            "isActive": true
        }
    ]
} */
    //return this.http.get<Servicio[]>(this.apiUrl, { headers });
    return this.http.get<{ data: Servicio[] }>(this.apiUrl, { headers }).pipe(
      map(response => response.data)
    );
  }
}
