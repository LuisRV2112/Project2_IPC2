import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common'; 
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  submitted = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email:    ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;

    if (this.loginForm.invalid) return;

    this.auth.login(this.loginForm.value).subscribe({
      next: res => {
        localStorage.setItem('user_id', res.user.id);
        localStorage.setItem('auth_token', res.token);
        localStorage.setItem('user_role', res.user.roleId);

        // Redirige a la página de bienvenida
        this.router.navigate(['/bienvenida']);
      },
      error: err => {
        this.error = 'Credenciales inválidas';
        console.error(err);
      }
    });
  }

  testBackend(): void {
    this.auth.testBackend().subscribe({
      next: res => console.log('✅ Backend responde:', res),
      error: err => console.error('❌ Error al conectar con el backend:', err)
    });
  }
}
