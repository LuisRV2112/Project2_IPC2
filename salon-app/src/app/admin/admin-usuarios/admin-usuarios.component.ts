import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Usuario, UsuariosService } from '../service/usuarios.service';

@Component({
  selector: 'app-admin-usuarios',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-usuarios.component.html',
  styleUrl: './admin-usuarios.component.scss'
})
export class AdminUsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuarioForm!: FormGroup;
  cargando: boolean = false;
  roleMap: { [key: number]: string } = {
    1: 'Administrador',
    2: 'Marketing',
    3: 'Encargado de servicios',
    4: 'Empleado',
    5: 'Cliente'
  };
  
  

  constructor(
    private fb: FormBuilder,
    private adminUsuariosService: UsuariosService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.usuarioForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      dpi: ['', Validators.required],
      phone: ['', Validators.required],
      address: ['', Validators.required],
      roleId: ['', Validators.required] 
    });
  
    this.obtenerUsuarios();
  }

  obtenerUsuarios() {
    this.adminUsuariosService.obtenerUsuarios().subscribe({
      next: (res) => {
        this.usuarios = res;
      },
      error: (err) => {
        console.error('Error al cargar usuarios', err);
      }
    });
  }

  crearUsuario() {
    if (this.usuarioForm.invalid) return;

    this.cargando = true;
    this.adminUsuariosService.crearUsuario(this.usuarioForm.value).subscribe({
      next: () => {
        alert('✅ Usuario creado exitosamente.');
        this.usuarioForm.reset();
        this.obtenerUsuarios();
        this.cargando = false;
      },
      error: (err) => {
        console.error(err);
        alert('❌ Error al crear usuario.');
        this.cargando = false;
      }
    });
  }

  darDeBaja(userId: number) {
    if (!confirm('¿Seguro que deseas dar de baja a este usuario?')) return;

    this.adminUsuariosService.darDeBajaUsuario(userId).subscribe({
      next: () => {
        alert('✅ Usuario dado de baja.');
        this.obtenerUsuarios();
      },
      error: (err) => {
        console.error(err);
        alert('❌ Error al dar de baja al usuario.');
      }
    });
  }

  regresarInicio() {
    this.router.navigate(['/bienvenida']);
  }
}
