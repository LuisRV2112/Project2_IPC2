import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AlertService } from '../auth/utils/alert.service';

@Component({
  selector: 'app-reservar-cita',
  standalone: true,
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
    private fb: FormBuilder,
    private alert: AlertService
  ) {}

  ngOnInit(): void {
    this.servicioId = Number(this.route.snapshot.paramMap.get('id'));

    this.reservaForm = this.fb.group({
      fecha: ['', Validators.required],       
      trabajadorId: ['', Validators.required],
      horario: ['', Validators.required]
    });

    this.http.get(`/api/services/employees/${this.servicioId}`).subscribe((res: any) => {
      this.trabajadores = res.employees;
    });
  }

  cargarHorarios() {
    if (!this.reservaForm.value.trabajadorId || !this.reservaForm.value.fecha) return;

    this.http.get<{ slots: string[] }>(`/api/availability`, {
      params: {
        employeeId: this.reservaForm.value.trabajadorId,
        serviceId: this.servicioId,
        date: this.reservaForm.value.fecha
      }
    }).subscribe((res) => {
      console.log(res.slots);
      this.horarios = res.slots;
      this.reservaForm.patchValue({ horario: '' });
    });
  }

  reservar() {
    if (this.reservaForm.invalid) return;
  
    const clientId = Number(localStorage.getItem('user_id'));
    if (!clientId) {
      this.alert.error('Error', 'No se pudo identificar al cliente.');
      return;
    }
  
    const slot = this.reservaForm.value.horario;
    if (!slot || !slot.includes(' - ')) {
      this.alert.error('Error', 'Selecciona un horario válido.');
      return;
    }
  
    const [startHour, endHour] = slot.split(' - ');
    const fecha = this.reservaForm.value.fecha; // yyyy-mm-dd
  
    const start = new Date(`${fecha}T${startHour}:00`);
    const end = new Date(`${fecha}T${endHour}:00`);
      
    const payload = {
      clientId: clientId,
      employeeId: +this.reservaForm.value.trabajadorId,
      serviceId: this.servicioId,
      startTime: this.toLocalISOString(start),
      endTime:   this.toLocalISOString(end)
    };
  
    this.http.post('/api/client/appointments', payload).subscribe({
      next: (res: any) => {
        this.alert.success('✅ Cita reservada con éxito', `Tu cita #${res.id} fue creada.`);
      },
      error: (e) => {
        console.error(e);
        this.alert.error('❌ Error al reservar cita', e.error?.error || 'Intenta más tarde.');
      }
    });
  }

 toLocalISOString(d: Date): string {
    const pad = (n: number) => n.toString().padStart(2, "0");
    const yyyy = d.getFullYear();
    const MM   = pad(d.getMonth() + 1);
    const dd   = pad(d.getDate());
    const hh   = pad(d.getHours());
    const mm   = pad(d.getMinutes());
    const ss   = pad(d.getSeconds());

    // offset en minutos; ej. -360
    const tzMin = -d.getTimezoneOffset();
    const sign  = tzMin >= 0 ? "+" : "-";
    const tzh   = pad(Math.floor(Math.abs(tzMin) / 60));
    const tzm   = pad(Math.abs(tzMin) % 60);

    return `${yyyy}-${MM}-${dd}T${hh}:${mm}:${ss}${sign}${tzh}:${tzm}`;
  }

  
}
