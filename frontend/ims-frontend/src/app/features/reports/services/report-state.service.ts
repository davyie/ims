import { Injectable, inject, signal } from '@angular/core';
import { EventProjectionDocument } from '../../../shared/models/models';
import { ReportApiService } from './report-api.service';

@Injectable({ providedIn: 'root' })
export class ReportStateService {
  private api = inject(ReportApiService);

  readonly events = signal<EventProjectionDocument[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly currentEntityId = signal<string | null>(null);

  loadMarketReport(marketId: string, type: 'SESSION' | 'STOCK_COMPARISON' = 'SESSION'): void {
    this.loading.set(true);
    this.currentEntityId.set(marketId);
    this.api.getMarketReport(marketId, type).subscribe({
      next: page => { this.events.set(page.content); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  loadWarehouseReport(warehouseId: string, type: 'INVENTORY_SNAPSHOT' | 'MOVEMENT_HISTORY' | 'LOW_STOCK' | 'VALUATION' = 'INVENTORY_SNAPSHOT'): void {
    this.loading.set(true);
    this.currentEntityId.set(warehouseId);
    this.api.getWarehouseReport(warehouseId, type).subscribe({
      next: page => { this.events.set(page.content); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  loadTransferReport(): void {
    this.loading.set(true);
    this.api.getTransferReport().subscribe({
      next: page => { this.events.set(page.content); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
