import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-reservar-cita',
  imports: [CommonModule, ReactiveFormsModule, NgIf, NgFor],
  templateUrl: './reservar-cita.component.html',
  styleUrl: './reservar-cita.component.scss'
})
export class ReservarCitaComponent implements OnInit {
  servicioId!: number;
  trabajadores: any[] = [];
  horarios: string[] = [];
  reservaForm!: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.servicioId = Number(this.route.snapshot.paramMap.get('id'));

    this.reservaForm = this.fb.group({
      trabajadorId: ['', Validators.required],
      horario: ['', Validators.required]
    });

    this.http.get(`/api/services/${this.servicioId}`).subscribe((res: any) => {
      this.trabajadores = res;
    });
  }

  cargarHorarios(trabajadorId: number) {
    this.http.get<string[]>(`/api/trabajadores/${trabajadorId}/horarios-disponibles?servicio=${this.servicioId}`)
      .subscribe((res) => {
        this.horarios = res;
        this.reservaForm.patchValue({ horario: '' }); // limpiar selección previa
      });
  }

  reservar() {
    if (this.reservaForm.invalid) return;

    const payload = {
      servicioId: this.servicioId,
      trabajadorId: this.reservaForm.value.trabajadorId,
      horario: this.reservaForm.value.horario,
    };

    this.http.post('/api/citas', payload).subscribe({
      next: () => alert('✅ Cita reservada con éxito'),
      error: () => alert('❌ Error al reservar cita')
    });
  }
}