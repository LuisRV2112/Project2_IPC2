import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Price {
  id?: number;
  pricePerDay: number;
  effectiveDate?: string;
}

export interface CurrentPriceResponse {
  currentPrice?: number;
  message?: string;
}

export interface HistoryResponse {
  history: Price[];
}

@Injectable({
  providedIn: 'root'
})
export class AdPricingService {
  private readonly apiUrl = 'api/ad-pricing';

  constructor(private http: HttpClient) {}

  createPrice(pricePerDay: number): Observable<{ message: string }> {
    return this.http
      .post<{ message: string }>(`${this.apiUrl}`, { pricePerDay })
      .pipe(catchError(this.handleError));
  }

  getCurrentPrice(): Observable<CurrentPriceResponse> {
    return this.http
      .get<CurrentPriceResponse>(`${this.apiUrl}`)
      .pipe(catchError(this.handleError));
  }

  getHistory(): Observable<HistoryResponse> {
    return this.http
      .get<HistoryResponse>(`${this.apiUrl}/history`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    return throwError(() => error);
  }
}
