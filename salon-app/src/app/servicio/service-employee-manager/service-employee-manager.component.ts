import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServiceEmployeeService, Employee } from '../service-employee.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-service-employee-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './service-employee-manager.component.html',
  styleUrls: ['./service-employee-manager.component.scss']
})
export class ServiceEmployeeManagerComponent implements OnInit {
  serviceId!: number;
  assignedEmployees: Employee[] = [];
  selectedEmployeeIds: number[] = [];
  mensaje: string | null = null;

  constructor(
    private serviceEmployeeService: ServiceEmployeeService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.serviceId = Number(this.route.snapshot.paramMap.get('serviceId'));
    this.cargarEmpleadosAsignados();
  }

  cargarEmpleadosAsignados(): void {
    this.serviceEmployeeService.obtenerEmpleadosAsignados(this.serviceId).subscribe({
      next: (res) => this.assignedEmployees = res.employees,
      error: () => this.mensaje = 'Error al cargar empleados asignados'
    });
  }

  asignarEmpleados(): void {
    this.serviceEmployeeService.asignarEmpleados(this.serviceId, this.selectedEmployeeIds).subscribe({
      next: (res) => {
        this.mensaje = res.message;
        this.selectedEmployeeIds = [];
        this.cargarEmpleadosAsignados();
      },
      error: (err) => this.mensaje = err.error?.error || 'Error al asignar empleados'
    });
  }

  desasignarEmpleado(employeeId: number): void {
    this.serviceEmployeeService.desasignarEmpleado(this.serviceId, employeeId).subscribe({
      next: (res) => {
        this.mensaje = res.message;
        this.cargarEmpleadosAsignados();
      },
      error: (err) => this.mensaje = err.error?.error || 'Error al desasignar empleado'
    });
  }

  handleEmployeeIdInput(value: string): void {
    this.selectedEmployeeIds = value
      .split(',')
      .map(e => +e.trim())
      .filter(e => !isNaN(e));
  }
}
