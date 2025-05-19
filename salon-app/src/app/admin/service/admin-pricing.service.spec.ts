import { TestBed } from '@angular/core/testing';

import { AdminPricingService } from './admin-pricing.service';

describe('AdminPricingService', () => {
  let service: AdminPricingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminPricingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
