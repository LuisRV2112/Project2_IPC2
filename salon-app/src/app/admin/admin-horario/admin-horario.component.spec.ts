import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminHorarioComponent } from './admin-horario.component';

describe('AdminHorarioComponent', () => {
  let component: AdminHorarioComponent;
  let fixture: ComponentFixture<AdminHorarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminHorarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminHorarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
