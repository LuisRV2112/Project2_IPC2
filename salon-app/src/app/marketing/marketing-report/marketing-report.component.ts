import { Component } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-marketing-report',
  templateUrl: './marketing-report.component.html',
  styleUrls: ['./marketing-report.component.scss'],
  imports: [FormsModule]
})
export class MarketingReportComponent {
  tipoReporte: string = 'top-ads';
  fechaInicio: string = '';
  fechaFin: string = '';

  constructor(private http: HttpClient) {}

  generarReporte() {
    let params = new HttpParams();
    if (this.fechaInicio) {
      params = params.set('start', this.fechaInicio);
    }
    if (this.fechaFin) {
      params = params.set('end', this.fechaFin);
    }

    const endpoint = `api/marketing/reports/${this.tipoReporte}`;
    const nombreArchivo = this.obtenerNombreArchivo(this.tipoReporte);

    this.http.get(endpoint, { params, responseType: 'blob' }).subscribe({
      next: (data) => {
        const url = window.URL.createObjectURL(data);
        window.open(url, '_blank');
        /* const a = document.createElement('a');
        a.href = url;
        a.download = nombreArchivo;
        a.click();
        window.URL.revokeObjectURL(url); */
      },
      error: (err) => {
        console.error('Error al generar el reporte:', err);
        alert('Ocurrió un error al generar el reporte. Por favor, intente nuevamente.');
      }
    });
  }

  obtenerNombreArchivo(tipo: string): string {
    switch (tipo) {
      case 'top-ads':
        return 'top_anuncios_mostrados.pdf';
      case 'least-ads':
        return 'anuncios_menos_mostrados.pdf';
      case 'ad-usage-history':
        return 'historial_uso_anuncios.pdf';
      default:
        return 'reporte.pdf';
    }
  }
}
