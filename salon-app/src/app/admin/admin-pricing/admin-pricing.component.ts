import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdPricingService, Price, CurrentPriceResponse, HistoryResponse } from '../service/admin-pricing.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-admin-pricing',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, FormsModule],
  templateUrl: './admin-pricing.component.html',
  styleUrl: './admin-pricing.component.scss'
})
export class AdminPricingComponent implements OnInit {
  currentPrice: number | null = null;
  newPrice: number | null = null;
  history: Price[] = [];
  mensaje: string | null = null;
  error: string | null = null;

  constructor(private pricingService: AdPricingService) {}

  ngOnInit(): void {
    this.obtenerPrecioActual();
    this.obtenerHistorial();
  }

  crearPrecio(): void {
    this.error = this.mensaje = null;
    if (this.newPrice === null) {
      this.error = 'El campo pricePerDay es obligatorio';
      return;
    }

    this.pricingService.createPrice(this.newPrice).subscribe({
      next: res => {
        this.mensaje = res.message;
        this.newPrice = null;
        this.obtenerPrecioActual();
        this.obtenerHistorial();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 400) {
          this.error = err.error?.error || 'Petición mal formada';
        } else if (err.status === 403) {
          this.error = 'Acceso denegado';
        } else {
          this.error = 'Error al insertar el precio';
        }
      }
    });
  }

  obtenerPrecioActual(): void {
    this.pricingService.getCurrentPrice().subscribe({
      next: (res: CurrentPriceResponse) => {
        this.currentPrice = res.currentPrice ?? null;
        if (res.message) this.mensaje = res.message;
      },
      error: () => this.error = 'Error al obtener el precio actual'
    });
  }

  obtenerHistorial(): void {
    this.pricingService.getHistory().subscribe({
      next: (res: HistoryResponse) => this.history = res.history,
      error: () => this.error = 'Error al obtener el historial'
    });
  }
}