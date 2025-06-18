import { TestBed } from '@angular/core/testing';

import { WarehouseDetailsService } from './warehouse-details.service';

describe('WarehouseDetailsService', () => {
  let service: WarehouseDetailsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WarehouseDetailsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
