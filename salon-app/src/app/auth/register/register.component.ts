import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common'; 
import { AuthService } from '../auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})

export class RegisterComponent {
  registerForm!: FormGroup;
  submitted = false;

  constructor(private fb: FormBuilder, private auth: AuthService) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      dpi:       ['', [Validators.required, Validators.pattern('^[0-9]{13}$')]],
      phone:  ['', [Validators.required, Validators.pattern('^[0-9]{8,10}$')]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      email:     ['', [Validators.required, Validators.email]],
      password:  ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get f() { return this.registerForm.controls; }

  onSubmit(): void {
    this.submitted = true;
    if (this.registerForm.invalid) return;
    this.auth.register(this.registerForm.value).subscribe({
      next: () => alert('Registro exitoso'),
      error: err => console.error(err)
    });
  }
}
