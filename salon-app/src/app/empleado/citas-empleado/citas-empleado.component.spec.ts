import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CitasEmpleadoComponent } from './citas-empleado.component';

describe('CitasEmpleadoComponent', () => {
  let component: CitasEmpleadoComponent;
  let fixture: ComponentFixture<CitasEmpleadoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CitasEmpleadoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CitasEmpleadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
