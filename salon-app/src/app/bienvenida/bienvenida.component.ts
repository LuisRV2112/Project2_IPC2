import { Component, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { ClienteService } from '../cliente/cliente.service';
import { ServiceService, Servicio } from '../servicio/service.service';
import { AdminServiciosComponent } from '../servicio/admin-servicios/admin-servicios.component';
import { AdsComponent } from '../ads/ads/ads.component';

@Component({
  selector: 'app-bienvenida',
  imports: [CommonModule, NgIf, NgFor, AsyncPipe, AdminServiciosComponent, AdsComponent],
  templateUrl: './bienvenida.component.html',
  styleUrl: './bienvenida.component.scss',
})
export class BienvenidaComponent implements OnInit {
  servicios$!: Observable<Servicio[]>;
  isAdmin = false;
  isEmpleado = false;
  isEncargadoServicios = false;
  isMarketing = false;
  constructor(
    private router: Router,
    private clienteService: ClienteService,
    private serviceService: ServiceService
  ) {}

  ngOnInit(): void {
    const role = localStorage.getItem('user_role');
    this.isAdmin = role == '1';
    this.isEmpleado = role == '4';
    this.isEncargadoServicios = role == '3';
    this.isMarketing = role == '2';

    if (!this.isAdmin && !this.isEmpleado && !this.isEncargadoServicios && !this.isMarketing) {
      this.servicios$ = this.serviceService.obtenerServicios();
    }
  }

  verCatalogo(servicioId: number): void {
    if (servicioId) {
      this.serviceService.descargarCatalogo(servicioId).subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          window.open(url, '_blank');
        },
        error: (err) => {
          console.error('Error al obtener el PDF:', err);
        }
      });
    }

  }
  
  reservar(servicioId: number): void {
    this.router.navigate(['/reservar', servicioId]); 
  }

  navegar(ruta: string): void {
    console.log('Navegando a:', ruta);
    this.router.navigate([ruta]);
  }

  verIntereses(): void {
    this.router.navigate(['/cliente/intereses']);
  }

  verCitas(): void {
    this.router.navigate(['/cliente/citas']);
  }
  
  verCitasEmpleado(): void {
    this.router.navigate(['/empleado/citas']);
  }

  verPerfil(): void {
    this.router.navigate(['empleado/perfil']);
  }
  
  verFacturasCliente(): void {
    this.router.navigate(['/cliente/facturas']);
  }
  
  verFacturasEmpleado(): void {
    this.router.navigate(['/empleado/facturas']);
  }

  descargarReporte(tipo: 'top-most' | 'top-least' | 'top-income'): void {
    this.serviceService.descargarReporte(tipo).subscribe({
      next: (blob) => {
        const nombres: any = {
          'top-most': 'servicios_mas_reservados.pdf',
          'top-least': 'servicios_menos_reservados.pdf',
          'top-income': 'servicio_mas_ingresos.pdf'
        };
  
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
        /* const a = document.createElement('a');
        a.href = url;
        a.download = nombres[tipo] || 'reporte.pdf';
        a.click();
        window.URL.revokeObjectURL(url); */
      },
      error: () => {
        alert('Ocurrió un error al descargar el reporte');
      }
    });
  }
  
}
