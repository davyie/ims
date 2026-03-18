import { Injectable, inject, signal, computed } from '@angular/core';
import { Market, Transaction, AllMarketsSummary, Item } from '../../../shared/models/models';
import { MarketApiService } from '../../markets/services/market-api.service';
import { ItemApiService } from '../../items/services/item-api.service';
import { forkJoin } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DashboardStateService {
  private marketApi = inject(MarketApiService);
  private itemApi = inject(ItemApiService);

  readonly openMarkets = signal<Market[]>([]);
  readonly allItems = signal<Item[]>([]);
  readonly recentTransactions = signal<Transaction[]>([]);
  readonly allSummary = signal<AllMarketsSummary | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly lowStockCount = computed(() =>
    this.allItems().filter(i => i.totalStorageStock <= 5).length
  );

  readonly openMarketCount = computed(() => this.openMarkets().length);

  loadDashboard(): void {
    this.loading.set(true);
    forkJoin({
      openMarkets: this.marketApi.getMarkets('OPEN'),
      allItems: this.itemApi.getItems(),
      allSummary: this.marketApi.getAllMarketsSummary(),
    }).subscribe({
      next: ({ openMarkets, allItems, allSummary }) => {
        this.openMarkets.set(openMarkets);
        this.allItems.set(allItems);
        this.allSummary.set(allSummary);
        this.loading.set(false);
      },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
