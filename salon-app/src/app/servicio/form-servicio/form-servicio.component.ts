import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ServiceService, Servicio } from '../service.service';

@Component({
  selector: 'app-form-servicio',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './form-servicio.component.html',
  styleUrl: './form-servicio.component.scss',
})
export class FormServicioComponent implements OnInit {
  servicioForm: FormGroup;
  esEdicion = false;
  servicioId!: number;
  errorMessage: string | null = null;
  archivoCatalogo: File | null = null;
  mensajeCatalogo: string | null = null;
  errorCatalogo: string | null = null;
  catalogoUrl: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private serviceService: ServiceService
  ) {
    this.servicioForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      imageUrl: ['', Validators.required],
      durationMin: [0, [Validators.required, Validators.min(1)]],
      price: [0, [Validators.required, Validators.min(0)]],
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.esEdicion = !!idParam;

    if (this.esEdicion) {
      this.servicioId = Number(idParam);
      this.serviceService.obtenerServicio(this.servicioId).subscribe({
        next: (data) => {
          const servicio = data.data as Servicio;
          this.servicioForm.patchValue({
            name: servicio.name,
            description: servicio.description,
            imageUrl: servicio.imageUrl,
            durationMin: servicio.durationMin,
            price: servicio.price
          });
          console.log('Servicio cargado:', servicio);
        
          if (servicio.catalogoPdfUrl) {
            this.catalogoUrl = servicio.catalogoPdfUrl;
            this.mensajeCatalogo = 'Catálogo ya cargado: ' + servicio.catalogoPdfUrl.split('/').pop();
          }
        },
        error: () => this.errorMessage = 'Error al cargar el servicio',
      });
    }
  }

  guardar(): void {
    if (this.servicioForm.invalid) return;

    const datos = this.servicioForm.value;
    
    const accion = this.esEdicion
      ? this.serviceService.actualizarServicio(this.servicioId, datos)
      : this.serviceService.crearServicio(datos);

    accion.subscribe({
      next: () => this.router.navigate(['/bienvenida']),
      error: (error) => {
        this.errorMessage = error?.error?.message || 'Error al guardar el servicio';
      }
    });
  }

  onArchivoSeleccionado(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.size > 5 * 1024 * 1024) {
        this.errorCatalogo = 'El archivo supera los 5MB.';
        this.archivoCatalogo = null;
      } else {
        this.archivoCatalogo = file;
        this.errorCatalogo = null;
      }
    }
  }
  
  subirCatalogo() {
    if (!this.archivoCatalogo || !this.servicioId) return;
  
    this.serviceService.subirCatalogo(this.servicioId, this.archivoCatalogo).subscribe({
      next: () => {
        this.mensajeCatalogo = 'Catálogo subido correctamente';
        this.errorCatalogo = null;
      },
      error: (err) => {
        this.errorCatalogo = err?.error?.message || 'Error al subir el catálogo';
        this.mensajeCatalogo = null;
      }
    });
  }

  verCatalogoExistente() {
    if (this.servicioId) {
      this.serviceService.descargarCatalogo(this.servicioId).subscribe({
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
  
}
