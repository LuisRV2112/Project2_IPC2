import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmpleadoService } from '../empleado.service';

@Component({
  selector: 'app-perfil-empleado',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './perfil-empleado.component.html',
  styleUrl: './perfil-empleado.component.scss'
})
export class PerfilEmpleadoComponent implements OnInit {
  perfil: any = {
    name: '',
    description: '',
    hobbies: '',
    photoUrl: ''
  };

  constructor(private empleadoService: EmpleadoService) {}

  ngOnInit(): void {
    this.empleadoService.obtenerPerfil().subscribe(data => {
      console.log(data);
      this.perfil = { ...data.profile };
    });
  }

  guardarCambios(): void {
    this.empleadoService.actualizarPerfil(this.perfil).subscribe({
      next: () => alert('Perfil actualizado exitosamente'),
      error: (err) => console.error('Error al actualizar perfil:', err)
    });
  }
}
