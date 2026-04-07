import { Injectable, inject, signal, computed } from '@angular/core';
import { Market, Item } from '../../../shared/models/models';
import { MarketApiService } from '../../markets/services/market-api.service';
import { ItemApiService } from '../../items/services/item-api.service';
import { forkJoin } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DashboardStateService {
  private marketApi = inject(MarketApiService);
  private itemApi = inject(ItemApiService);

  readonly markets = signal<Market[]>([]);
  readonly allItems = signal<Item[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly openMarkets = computed(() => this.markets().filter(m => m.status === 'OPEN'));
  readonly openMarketCount = computed(() => this.openMarkets().length);
  readonly totalItemCount = computed(() => this.allItems().length);

  loadDashboard(): void {
    this.loading.set(true);
    forkJoin({
      markets: this.marketApi.getMarkets(),
      items: this.itemApi.getItems(),
    }).subscribe({
      next: ({ markets, items }) => {
        this.markets.set(markets.content);
        this.allItems.set(items.content);
        this.loading.set(false);
      },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
