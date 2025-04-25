import { Routes } from '@angular/router';
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent }    from './auth/login/login.component';
import { BienvenidaComponent } from './cliente/bienvenida/bienvenida.component';
import { ReservarCitaComponent } from './reserva/reservar-cita.component';

export const routes: Routes = [
    { path: 'register', component: RegisterComponent },
    { path: 'login',    component: LoginComponent    },
    { path: 'bienvenida',  component: BienvenidaComponent },
    { path: 'reservar/:id', component: ReservarCitaComponent },
    { path: '**',          redirectTo: 'login' }
];
