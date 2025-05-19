import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../cliente.service';

@Component({
  selector: 'app-intereses',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './intereses.component.html',
  styleUrl: './intereses.component.scss'
})
export class InteresesComponent implements OnInit {
  nuevoInteres: string = '';
  intereses: string[] = [];

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.clienteService.obtenerIntereses().subscribe({
      next: (intereses) => {
        this.intereses = intereses;
      },
      error: (err) => {
        console.error(err);
        alert('Error al cargar los intereses');
      }
    });
  }

  agregarInteres(): void {
    if (this.nuevoInteres.trim()) {
      this.clienteService.agregarInteres(this.nuevoInteres).subscribe({
        next: () => {
          this.intereses.push(this.nuevoInteres);
          this.nuevoInteres = '';
          alert('Interés agregado exitosamente');
        },
        error: (err) => {
          console.error(err);
          alert('Error al agregar el interés');
        }
      });
    }
  }

  eliminarInteres(interest: string): void {
    this.clienteService.eliminarInteres(interest).subscribe({
      next: () => {
        this.intereses = this.intereses.filter(i => i !== interest);
        alert('Interés eliminado exitosamente');
      },
      error: (err) => {
        console.error(err);
        alert('Error al eliminar el interés');
      }
    });
  }
}