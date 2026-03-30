import { Injectable, inject, signal } from '@angular/core';
import { MarketSummary, AllMarketsSummary } from '../../../shared/models/models';
import { ReportApiService } from './report-api.service';

@Injectable({ providedIn: 'root' })
export class ReportStateService {
  private api = inject(ReportApiService);

  readonly marketSummary = signal<MarketSummary | null>(null);
  readonly allSummary = signal<AllMarketsSummary | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadMarketSummary(id: string): void {
    this.loading.set(true);
    this.api.getMarketSummary(id).subscribe({
      next: s => { this.marketSummary.set(s); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  loadAllSummary(status?: string): void {
    this.loading.set(true);
    this.api.getAllMarketsSummary(status).subscribe({
      next: s => { this.allSummary.set(s); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
