import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClienteService, Cita } from '../cliente.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-citas-cliente',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './citas-cliente.component.html',
  styleUrl: './citas-cliente.component.scss'
})
export class CitasClienteComponent implements OnInit {
  citas$!: Observable<Cita[]>;
  mensaje: string | null = null;

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.cargarCitas();
  }

  cargarCitas() {
    this.citas$ = this.clienteService.obtenerCitasCliente();
  }
  
  cancelarCita(citaId: number) {
    this.clienteService.cancelarCita(citaId).subscribe({
      next: (res) => {
        this.mensaje = res.message;
        this.cargarCitas();
      },
      error: (err) => {
        this.mensaje = err.error?.error || 'Error al cancelar cita.';
      }
    });
  }
}