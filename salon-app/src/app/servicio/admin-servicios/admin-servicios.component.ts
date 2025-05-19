import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ServiceService, Servicio } from '../service.service';
import { CommonModule } from '@angular/common';

@Component({
  imports: [CommonModule],
  selector: 'app-admin-servicios',
  templateUrl: './admin-servicios.component.html',
  styleUrls: ['./admin-servicios.component.scss']
})
export class AdminServiciosComponent implements OnInit {
  servicios: Servicio[] = [];
  errorMessage: string | null = null;

  constructor(private serviceService: ServiceService, private router: Router) {}

  ngOnInit(): void {
    this.cargarServicios();
  }

  cargarServicios(): void {
    this.serviceService.obtenerServicios().subscribe({
      next: (data) => this.servicios = data,
      error: () => this.errorMessage = 'Error al cargar los servicios.'
    });
  }

  editar(id: number): void {
    this.router.navigate(['/servicio/editar', id]);
  }

  cambiarEstado(id: number, nuevoEstado: boolean): void {
    this.serviceService.cambiarEstadoServicio(id, nuevoEstado).subscribe({
      next: () => this.cargarServicios(),
      error: () => this.errorMessage = 'Error al actualizar el estado del servicio.'
    });
  }
  asignarEmpleados(id: number): void {
    this.router.navigate(['/servicio/empleados', id]);
  }
}
