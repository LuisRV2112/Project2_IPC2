import { Component, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Servicio, ServiceService } from '../../service/service.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-bienvenida',
  imports: [CommonModule, NgIf, NgFor, AsyncPipe],
  templateUrl: './bienvenida.component.html',
  styleUrl: './bienvenida.component.scss',
  
})
export class BienvenidaComponent implements OnInit {
  servicios$!: Observable<Servicio[]>;

  constructor(private serviciosService: ServiceService) {}

  ngOnInit(): void {
    this.servicios$ = this.serviciosService.obtenerServicios();
  }

  verCatalogo(url: string): void {
    window.open(url, '_blank');
  }
  
  reservar(servicioId: number): void {
    // Aquí podrías redirigir a la pantalla de selección de trabajador y horario
    console.log(`Reservar servicio con ID: ${servicioId}`);
    // this.router.navigate(['/reservar', servicioId]); // ejemplo
  }
}