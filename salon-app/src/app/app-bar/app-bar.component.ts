import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { AnunciosClienteComponent } from '../ads/anuncios-cliente/anuncios-cliente.component';

@Component({
  selector: 'app-app-bar',
  standalone: true,
  templateUrl: './app-bar.component.html',
  styleUrls: ['./app-bar.component.scss'],
  imports: [CommonModule, AnunciosClienteComponent],
})
export class AppBarComponent implements OnInit {
  @Input() isAdmin = false;
  @Input() isEncargadoServicios = false;
  @Input() isMarketing = false;
  @Input() isEmpleado = false;
  @Input() isUsuario = false;
  isLoggedIn = false;
  role = null as string | null;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.authService.isLoggedIn$.subscribe((loggedIn) => {
      this.role = this.authService.getRole();
      switch (this.role) {
        case '1':
          this.isAdmin = true;
          break;
        case '2':
          this.isEncargadoServicios = true;
          break;
        case '3':
          this.isMarketing = true;
          break;
        case '4':
          this.isEmpleado = true;
          break;
        case '5':
          this.isUsuario = true;
          break;
        default:
          this.isAdmin = false;
          this.isEncargadoServicios = false;
          this.isMarketing = false;
          this.isEmpleado = false;
          this.isUsuario = false;
          break;
      }
      this.isLoggedIn = loggedIn;
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navegar(ruta: string) {
    this.router.navigate([ruta]);
  }

  navegarHome() {
    this.router.navigate(['/bienvenida']);
  }


  verCitasEmpleado() {}
  verFacturasEmpleado() {}
  verIntereses() {}
  verCitas() {}
}