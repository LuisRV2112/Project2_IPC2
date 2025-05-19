import { TestBed } from '@angular/core/testing';

import { ServiceEmployeeService } from './service-employee.service';

describe('ServiceEmployeeService', () => {
  let service: ServiceEmployeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServiceEmployeeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
