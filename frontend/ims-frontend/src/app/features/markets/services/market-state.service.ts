import { Injectable, inject, signal } from '@angular/core';
import { Market, CreateMarketRequest } from '../../../shared/models/models';
import { MarketApiService } from './market-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable({ providedIn: 'root' })
export class MarketStateService {
  private api = inject(MarketApiService);
  private notify = inject(NotificationService);

  readonly markets = signal<Market[]>([]);
  readonly selectedMarket = signal<Market | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadMarkets(status?: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getMarkets(status).subscribe({
      next: markets => { this.markets.set(markets); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  loadMarket(id: string): void {
    this.loading.set(true);
    this.api.getMarket(id).subscribe({
      next: market => { this.selectedMarket.set(market); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  createMarket(cmd: CreateMarketRequest): Promise<Market> {
    return new Promise((resolve, reject) => {
      this.api.createMarket(cmd).subscribe({
        next: market => {
          this.markets.update(ms => [...ms, market]);
          this.notify.success(`Market "${market.name}" created`);
          resolve(market);
        },
        error: err => { reject(err); }
      });
    });
  }

  openMarket(id: string): Promise<Market> {
    return new Promise((resolve, reject) => {
      this.api.openMarket(id).subscribe({
        next: market => {
          this.markets.update(ms => ms.map(m => m.id === id ? market : m));
          this.selectedMarket.set(market);
          this.notify.success(`Market "${market.name}" opened`);
          resolve(market);
        },
        error: err => { reject(err); }
      });
    });
  }

  closeMarket(id: string, createdBy?: string): Promise<Market> {
    return new Promise((resolve, reject) => {
      this.api.closeMarket(id, createdBy).subscribe({
        next: market => {
          this.markets.update(ms => ms.map(m => m.id === id ? market : m));
          this.selectedMarket.set(market);
          this.notify.success(`Market "${market.name}" closed`);
          resolve(market);
        },
        error: err => { reject(err); }
      });
    });
  }
}
