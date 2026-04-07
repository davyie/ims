import { Injectable, inject, signal, computed } from '@angular/core';
import { forkJoin } from 'rxjs';
import { StorageItem, Item, WarehouseStock } from '../../../shared/models/models';
import { WarehouseApiService } from './warehouse-api.service';
import { ItemApiService } from '../../items/services/item-api.service';

@Injectable({ providedIn: 'root' })
export class StorageStateService {
  private warehouseApi = inject(WarehouseApiService);
  private itemApi = inject(ItemApiService);

  readonly storageItems = signal<StorageItem[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly lowStockItems = computed(() => this.storageItems().filter(i => i.currentStock === 0));

  loadStorageItems(): void {
    this.loading.set(true);
    this.error.set(null);

    // First load warehouses, then load stock for the first warehouse + items
    this.warehouseApi.listWarehouses().subscribe({
      next: warehousePage => {
        const warehouses = warehousePage.content;
        if (warehouses.length === 0) {
          this.storageItems.set([]);
          this.loading.set(false);
          return;
        }

        // Load all stock across all warehouses and items
        const stockRequests = warehouses.map(w => this.warehouseApi.listStock(w.warehouseId));
        forkJoin([this.itemApi.getItems(), ...stockRequests]).subscribe({
          next: ([itemPage, ...stockPages]) => {
            const items = (itemPage as Awaited<typeof itemPage>).content as Item[];
            const itemMap = new Map<string, Item>(items.map(i => [i.itemId, i]));

            const combined: StorageItem[] = [];
            (stockPages as Array<{ content: WarehouseStock[] }>).forEach((stockPage, idx) => {
              stockPage.content.forEach((stock: WarehouseStock) => {
                const item = itemMap.get(stock.itemId);
                combined.push({
                  itemId: stock.itemId,
                  sku: item?.sku ?? stock.itemId.slice(0, 8),
                  name: item?.name ?? 'Unknown',
                  category: item?.category,
                  currentStock: stock.quantity,
                  binLocation: stock.binLocation,
                  warehouseId: warehouses[idx].warehouseId,
                });
              });
            });

            this.storageItems.set(combined);
            this.loading.set(false);
          },
          error: err => { this.error.set(err.message); this.loading.set(false); }
        });
      },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
