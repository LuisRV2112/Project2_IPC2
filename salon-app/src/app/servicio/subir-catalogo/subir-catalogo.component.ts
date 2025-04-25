import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServiceService } from '../service.service';

@Component({
  selector: 'app-subir-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subir-catalogo.component.html',
  styleUrl: './subir-catalogo.component.scss'
})
export class SubirCatalogoComponent {
  @Input() serviceId!: number;
  archivoSeleccionado: File | null = null;
  mensaje: string | null = null;
  error: string | null = null;

  constructor(private serviceService: ServiceService) {}

  seleccionarArchivo(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.size > 5 * 1024 * 1024) {
        this.error = 'El archivo supera el límite de 5MB.';
        this.archivoSeleccionado = null;
      } else {
        this.archivoSeleccionado = file;
        this.error = null;
      }
    }
  }

  subirCatalogo() {
    if (!this.archivoSeleccionado || !this.serviceId) return;

    this.serviceService.subirCatalogo(this.serviceId, this.archivoSeleccionado).subscribe({
      next: () => this.mensaje = 'Catálogo subido exitosamente',
      error: (err) => this.error = err?.error?.message || 'Error al subir catálogo',
    });
  }
}
