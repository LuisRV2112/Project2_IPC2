import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Ad, AdsService } from '../ads.service';

@Component({
  selector: 'app-ads',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ads.component.html',
  styleUrl: './ads.component.scss',
})
export class AdsComponent implements OnInit {
  adForm: FormGroup;
  ads: Ad[] = [];
  error: string | null = null;
  editMode = false;
  adEditingId: number | null = null;
  filtro: 'vigentes' | 'caducados' | 'todos' = 'todos';

  constructor(private fb: FormBuilder, private adsService: AdsService) {
    this.adForm = this.fb.group({
      type: ['text', Validators.required],
      contentUrl: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      category: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.filtro = 'vigentes';
    this.cargarAnuncios();
  }

  cargarAnuncios(): void {
    this.adsService.obtenerAnuncios().subscribe({
      next: (res) => {
        const hoy = new Date().toISOString().split('T')[0];
        this.ads = res.data.filter(ad => {
          if (this.filtro === 'vigentes') return ad.endDate >= hoy;
          if (this.filtro === 'caducados') return ad.endDate < hoy;
          return true;
        });
      },
      error: (e) => {
        this.error = 'Error al cargar los anuncios';
        console.error(e);
      },
    });
  }
  
  cambiarFiltro(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.filtro = target.value as 'vigentes' | 'caducados' | 'todos';
    this.cargarAnuncios();
  }

  crearOActualizarAnuncio(): void {
    if (this.adForm.invalid) return;
    const datos: Ad = this.adForm.value;

    if (this.editMode && this.adEditingId !== null) {
      this.adsService.actualizarAnuncio(this.adEditingId, datos).subscribe({
        next: () => {
          this.resetForm();
          this.cargarAnuncios();
        },
        error: () => this.error = 'Error al actualizar el anuncio',
      });
    } else {
      this.adsService.crearAnuncio(datos).subscribe({
        next: () => {
          this.resetForm();
          this.cargarAnuncios();
        },
        error: () => this.error = 'Error al crear el anuncio',
      });
    }
  }

  prepararEdicion(ad: Ad): void {
    this.adForm.patchValue(ad);
    this.editMode = true;
    this.adEditingId = ad.id!;
  }

  eliminarAnuncio(id: number): void {
    if (!confirm('¿Estás seguro de eliminar este anuncio?')) return;

    this.adsService.eliminarAnuncio(id).subscribe({
      next: () => this.cargarAnuncios(),
      error: () => alert('Error al eliminar el anuncio'),
    });
  }

  registrarPago(adId: number): void {
    const monto = parseFloat(prompt('Ingrese el monto del pago') || '0');
    if (isNaN(monto) || monto <= 0) return;

    this.adsService.registrarPago(adId, monto).subscribe({
      next: () => alert('Pago registrado con éxito'),
      error: () => alert('Error al registrar el pago'),
    });
  }

  republicar(adId: number): void {
    const nuevaFecha = new Date();
    const nuevaFechaFin = new Date();
    nuevaFechaFin.setDate(nuevaFecha.getDate() + 7);

    const inicio = nuevaFecha.toISOString().split('T')[0];
    const fin = nuevaFechaFin.toISOString().split('T')[0];

    this.adsService.republicarAnuncio(adId, inicio, fin).subscribe({
      next: () => this.cargarAnuncios(),
      error: () => alert('Error al republicar el anuncio'),
    });
  }

  estaCaducado(anuncio: Ad): boolean {
    const hoy = new Date().toISOString().split('T')[0];
    return anuncio.endDate < hoy;
  }

  resetForm(): void {
    this.adForm.reset({ type: 'text' });
    this.editMode = false;
    this.adEditingId = null;
  }
}
