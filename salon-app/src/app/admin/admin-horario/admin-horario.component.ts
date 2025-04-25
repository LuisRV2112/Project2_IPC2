import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { HorarioService, DiaHorario } from '../service/horario.service';
import { FormsModule } from '@angular/forms';



@Component({
  selector: 'app-admin-horario',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, FormsModule],
  templateUrl: './admin-horario.component.html',
  styleUrl: './admin-horario.component.scss'
})
export class AdminHorarioComponent implements OnInit {
  horarios: DiaHorario[] = [];
  diasSemana = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
  mensaje: string | null = null;

  constructor(private horarioService: HorarioService) {}

  ngOnInit(): void {
    this.cargarHorarios();
  }

  cargarHorarios(): void {
    this.horarioService.obtenerHorarioSalon().subscribe({
      next: (data) => {
        this.horarios = data.map((dia: DiaHorario) => ({
          ...dia,
          openingTime: this.convertirAHHMM(dia.openingTime),
          closingTime: this.convertirAHHMM(dia.closingTime)
        }));
      },
      error: () => {
        this.mensaje = 'Error al cargar horarios';
      }
    });
  }
  

  actualizarHorario(dia: DiaHorario): void {
    this.horarioService.actualizarHorario(dia).subscribe({
      next: () => this.mensaje = 'Horario actualizado exitosamente',
      error: (error) => {
        console.error('Error al actualizar horario:', error);
        if (error.status === 400) {

          this.mensaje = error.error.error || 'Error de datos';
        } else {
          this.mensaje = 'Error al actualizar horario';
        }
      }
    });
  }

  convertirAHHMM(hora: string): string {
    const date = new Date(`1970-01-01T${this.formatoHoraISO(hora)}`);
    const horas = date.getHours().toString().padStart(2, '0');
    const minutos = date.getMinutes().toString().padStart(2, '0');
    return `${horas}:${minutos}`;
  }
  
  formatoHoraISO(hora: string): string {
    const [time, meridian] = hora.split(' ');
    let [h, m, s] = time.split(':').map(Number);
    if (meridian.toLowerCase() === 'p.m.' && h < 12) h += 12;
    if (meridian.toLowerCase() === 'a.m.' && h === 12) h = 0;
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }
  
}
