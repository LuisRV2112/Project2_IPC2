import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmpleadoService, CitaEmpleado } from '../empleado.service';
import { Observable } from 'rxjs';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-citas-empleado',
  standalone: true,
  imports: [CommonModule,
    FormsModule],
  templateUrl: './citas-empleado.component.html',
  styleUrl: './citas-empleado.component.scss'
})
export class CitasEmpleadoComponent implements OnInit {
  citas$!: Observable<CitaEmpleado[]>;
  fechaSeleccionada: string = this.hoyISO();

  constructor(private empleadoService: EmpleadoService) {}

  ngOnInit(): void {
    this.cargarCitas();
  }

  hoyISO(): string {
    return new Date().toISOString().split('T')[0];
  }

  cargarCitas(): void {
    this.citas$ = this.empleadoService.obtenerCitas(this.fechaSeleccionada);
    //las cccitas$ son un observable, por lo que no es necesario suscribirse a ellas aquí.
    console.log('Citas cargadas:', this.citas$);
    
  }

  actualizarEstado(citaId: number, status: string, noShow: boolean = false): void {
    this.empleadoService.actualizarEstadoCita(citaId, status, noShow).subscribe(() => {
      this.cargarCitas();
    });
  }

  descargarFactura(invoiceId: number): void {
    this.empleadoService.obtenerFactura(invoiceId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      window.open(url, '_blank');
      /* const link = document.createElement('a');
      link.href = url;
      link.download = `factura_${invoiceId}.pdf`;
      link.click(); */
    });
  }
  
}
