import { Injectable, inject, signal } from '@angular/core';
import { MarketStock, StockOperationRequest, CreateTransferRequest, Transfer } from '../../../shared/models/models';
import { MarketStockApiService } from './market-stock-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable({ providedIn: 'root' })
export class MarketStockStateService {
  private api = inject(MarketStockApiService);
  private notify = inject(NotificationService);

  readonly marketStockItems = signal<MarketStock[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadMarketStock(marketId: string): void {
    this.loading.set(true);
    this.api.getMarketStock(marketId).subscribe({
      next: page => { this.marketStockItems.set(page.content); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  shiftToMarket(req: CreateTransferRequest): Promise<Transfer> {
    return new Promise((resolve, reject) => {
      this.api.shiftToMarket(req).subscribe({
        next: transfer => {
          this.notify.success('Item shift initiated');
          resolve(transfer);
        },
        error: err => reject(err)
      });
    });
  }

  increment(marketId: string, req: StockOperationRequest): Promise<MarketStock> {
    return new Promise((resolve, reject) => {
      this.api.incrementStock(marketId, req).subscribe({
        next: stock => {
          this.marketStockItems.update(items =>
            items.map(i => i.itemId === req.itemId ? stock : i)
          );
          this.notify.success('Stock incremented');
          resolve(stock);
        },
        error: err => reject(err)
      });
    });
  }

  decrement(marketId: string, req: StockOperationRequest): Promise<MarketStock> {
    return new Promise((resolve, reject) => {
      this.api.decrementStock(marketId, req).subscribe({
        next: stock => {
          this.marketStockItems.update(items =>
            items.map(i => i.itemId === req.itemId ? stock : i)
          );
          this.notify.success('Stock decremented');
          resolve(stock);
        },
        error: err => reject(err)
      });
    });
  }
}
