import { Injectable, inject, signal, computed } from '@angular/core';
import { forkJoin } from 'rxjs';
import { StorageItem, WarehouseStock } from '../../../shared/models/models';
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

    // Load items and warehouses in parallel first
    forkJoin([this.itemApi.getItems(), this.warehouseApi.listWarehouses()]).subscribe({
      next: ([itemPage, warehousePage]) => {
        const items = itemPage.content;
        const warehouses = warehousePage.content;

        if (warehouses.length === 0) {
          // No warehouses yet — still show all items with zero stock
          this.storageItems.set(items.map(item => ({
            itemId: item.itemId,
            sku: item.sku,
            name: item.name,
            category: item.category,
            currentStock: 0,
            warehouseId: '',
          })));
          this.loading.set(false);
          return;
        }

        // Load stock for every warehouse in parallel
        forkJoin(warehouses.map(w => this.warehouseApi.listStock(w.warehouseId))).subscribe({
          next: stockPages => {
            // Build a map: itemId -> aggregated stock across all warehouses
            const stockMap = new Map<string, { quantity: number; binLocation?: string; warehouseId: string }>();
            stockPages.forEach((stockPage, idx) => {
              stockPage.content.forEach((stock: WarehouseStock) => {
                const existing = stockMap.get(stock.itemId);
                if (existing) {
                  existing.quantity += stock.quantity;
                } else {
                  stockMap.set(stock.itemId, {
                    quantity: stock.quantity,
                    binLocation: stock.binLocation,
                    warehouseId: warehouses[idx].warehouseId,
                  });
                }
              });
            });

            // Every item appears — stock defaults to 0 if no warehouse record exists
            this.storageItems.set(items.map(item => {
              const stock = stockMap.get(item.itemId);
              return {
                itemId: item.itemId,
                sku: item.sku,
                name: item.name,
                category: item.category,
                currentStock: stock?.quantity ?? 0,
                binLocation: stock?.binLocation,
                warehouseId: stock?.warehouseId ?? warehouses[0].warehouseId,
              };
            }));
            this.loading.set(false);
          },
          error: err => { this.error.set(err.message); this.loading.set(false); }
        });
      },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
