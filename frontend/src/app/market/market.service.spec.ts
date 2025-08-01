import { TestBed } from '@angular/core/testing';

import { MarketService } from './market.service';

describe('MarketServiceService', () => {
  let service: MarketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MarketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
