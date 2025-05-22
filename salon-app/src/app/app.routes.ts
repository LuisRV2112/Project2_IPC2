import { Routes } from '@angular/router';
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent }    from './auth/login/login.component';
import { BienvenidaComponent } from './bienvenida/bienvenida.component';
import { ReservarCitaComponent } from './reserva/reservar-cita.component';
import { AdminHorarioComponent } from './admin/admin-horario/admin-horario.component';
import { AdminUsuariosComponent } from './admin/admin-usuarios/admin-usuarios.component';
import { InteresesComponent } from './cliente/intereses/intereses.component';
import { CitasClienteComponent } from './cliente/citas-cliente/citas-cliente.component';
import { CitasEmpleadoComponent } from './empleado/citas-empleado/citas-empleado.component';
import { PerfilEmpleadoComponent } from './empleado/perfil-empleado/perfil-empleado.component';
import { FormServicioComponent } from './servicio/form-servicio/form-servicio.component';
import { AdminReportesComponent } from './admin/admin-reportes/admin-reportes.component';

import { AuthGuard } from './auth/auth.guard';
import { AdminPricingComponent } from './admin/admin-pricing/admin-pricing.component';
import { ServiceEmployeeManagerComponent } from './servicio/service-employee-manager/service-employee-manager.component';
import { MarketingReportComponent } from './marketing/marketing-report/marketing-report.component';

export const routes: Routes = [
    { path: 'register', component: RegisterComponent, canActivate: [AuthGuard] },
    { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },

  { path: 'bienvenida', component: BienvenidaComponent, canActivate: [AuthGuard] },
  { path: 'reservar/:id', component: ReservarCitaComponent, canActivate: [AuthGuard] },

  // Clientes
  { path: 'cliente/intereses', component: InteresesComponent, canActivate: [AuthGuard] },
  { path: 'cliente/citas', component: CitasClienteComponent, canActivate: [AuthGuard] },

  // Admin
  { path: 'admin/horario', component: AdminHorarioComponent, canActivate: [AuthGuard] },
  { path: 'admin/usuarios', component: AdminUsuariosComponent, canActivate: [AuthGuard] },
  { path: 'admin/reportes', component: AdminReportesComponent, canActivate: [AuthGuard] },
  { path: 'admin/precio-ads', component: AdminPricingComponent, canActivate: [AuthGuard] },

  // Empleado
  { path: 'empleado/citas', component: CitasEmpleadoComponent, canActivate: [AuthGuard] },
  { path: 'empleado/perfil', component: PerfilEmpleadoComponent, canActivate: [AuthGuard] },

  // Servicios
  { path: 'servicio/nuevo', component: FormServicioComponent, canActivate: [AuthGuard] },
  { path: 'servicio/editar/:id', component: FormServicioComponent, canActivate: [AuthGuard] },
  { path: 'servicio/empleados/:serviceId', component: ServiceEmployeeManagerComponent, canActivate: [AuthGuard] },

  //Marketing
  { path: 'marketing/reportes', component: MarketingReportComponent, canActivate: [AuthGuard] },

  { path: '**', redirectTo: 'login' },
];
