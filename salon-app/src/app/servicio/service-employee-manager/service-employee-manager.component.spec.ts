import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceEmployeeManagerComponent } from './service-employee-manager.component';

describe('ServiceEmployeeManagerComponent', () => {
  let component: ServiceEmployeeManagerComponent;
  let fixture: ComponentFixture<ServiceEmployeeManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceEmployeeManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceEmployeeManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
