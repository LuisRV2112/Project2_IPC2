import { Component } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  imports: [ReactiveFormsModule, FormsModule],
  selector: 'app-admin-reportes',
  templateUrl: './admin-reportes.component.html',
  styleUrls: ['./admin-reportes.component.scss'],
  standalone: true,
})
export class AdminReportesComponent {
  startDate: string = '';
  endDate: string = '';
  filterId: string = '';
  baseUrl = '/api/admin/reports/';

  constructor(private http: HttpClient) {}

  descargarReporte(endpoint: string, nombreArchivo: string, incluyeFiltro = false) {
    let params = new HttpParams();
    if (this.startDate) params = params.set('start', this.startDate);
    if (this.endDate) params = params.set('end', this.endDate);
    if (incluyeFiltro && this.filterId) params = params.set('filterId', this.filterId);

    this.http.get(`${this.baseUrl}${endpoint}`, { params, responseType: 'blob' }).subscribe({
      next: (pdf) => this.descargarBlob(pdf, nombreArchivo),
      error: () => alert(`Error al descargar el reporte ${nombreArchivo}`),
    });
  }

  private descargarBlob(blob: Blob, nombreArchivo: string) {
    const url = window.URL.createObjectURL(blob);
    window.open(url, '_blank');
 /*    const a = document.createElement('a');
    a.href = url;
    a.download = nombreArchivo;
    a.click();
    window.URL.revokeObjectURL(url); */
  }
}
