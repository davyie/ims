import { Injectable, inject, signal } from '@angular/core';
import { MarketItem, ShiftItemRequest, IncrementStockRequest, DecrementStockRequest, SetPriceRequest } from '../../../shared/models/models';
import { MarketStockApiService } from './market-stock-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable({ providedIn: 'root' })
export class MarketStockStateService {
  private api = inject(MarketStockApiService);
  private notify = inject(NotificationService);

  readonly marketItems = signal<MarketItem[]>([]);
  readonly selectedMarketItem = signal<MarketItem | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadMarketItems(marketId: string): void {
    this.loading.set(true);
    this.api.getMarketItems(marketId).subscribe({
      next: items => { this.marketItems.set(items); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  shiftItem(marketId: string, cmd: ShiftItemRequest): Promise<MarketItem> {
    return new Promise((resolve, reject) => {
      this.api.shiftItem(marketId, cmd).subscribe({
        next: item => {
          this.marketItems.update(items => {
            const exists = items.find(i => i.id === item.id);
            return exists ? items.map(i => i.id === item.id ? item : i) : [...items, item];
          });
          this.notify.success('Item shifted to market');
          resolve(item);
        },
        error: err => { reject(err); }
      });
    });
  }

  increment(marketId: string, itemId: string, cmd: IncrementStockRequest): Promise<MarketItem> {
    return new Promise((resolve, reject) => {
      this.api.incrementStock(marketId, itemId, cmd).subscribe({
        next: item => {
          this.marketItems.update(items => items.map(i => i.itemId === itemId ? item : i));
          this.selectedMarketItem.set(item);
          this.notify.success('Stock incremented');
          resolve(item);
        },
        error: err => { reject(err); }
      });
    });
  }

  decrement(marketId: string, itemId: string, cmd: DecrementStockRequest): Promise<MarketItem> {
    return new Promise((resolve, reject) => {
      this.api.decrementStock(marketId, itemId, cmd).subscribe({
        next: item => {
          this.marketItems.update(items => items.map(i => i.itemId === itemId ? item : i));
          this.selectedMarketItem.set(item);
          this.notify.success('Stock decremented');
          resolve(item);
        },
        error: err => { reject(err); }
      });
    });
  }

  setPrice(marketId: string, itemId: string, cmd: SetPriceRequest): Promise<MarketItem> {
    return new Promise((resolve, reject) => {
      this.api.setMarketItemPrice(marketId, itemId, cmd).subscribe({
        next: item => {
          this.marketItems.update(items => items.map(i => i.itemId === itemId ? item : i));
          this.selectedMarketItem.set(item);
          this.notify.success('Price updated');
          resolve(item);
        },
        error: err => { reject(err); }
      });
    });
  }
}
