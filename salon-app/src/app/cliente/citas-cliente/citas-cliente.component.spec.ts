import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CitasClienteComponent } from './citas-cliente.component';

describe('CitasClienteComponent', () => {
  let component: CitasClienteComponent;
  let fixture: ComponentFixture<CitasClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CitasClienteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CitasClienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
