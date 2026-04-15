import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Warehouse, CreateWarehouseRequest } from '../../../shared/models/models';
import { WarehouseApiService } from './warehouse-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable({ providedIn: 'root' })
export class WarehouseStateService {
  private api = inject(WarehouseApiService);
  private notify = inject(NotificationService);

  readonly warehouses = signal<Warehouse[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly defaultWarehouse = computed<Warehouse | null>(() => this.warehouses()[0] ?? null);
  readonly defaultWarehouseId = computed<string | null>(() => this.defaultWarehouse()?.warehouseId ?? null);

  loadWarehouses(): void {
    if (this.warehouses().length > 0) return; // already loaded
    this.loading.set(true);
    this.api.listWarehouses().subscribe({
      next: page => { this.warehouses.set(page.content); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  reload(): void {
    this.loading.set(true);
    this.api.listWarehouses().subscribe({
      next: page => { this.warehouses.set(page.content); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  async createWarehouse(req: CreateWarehouseRequest): Promise<Warehouse> {
    const warehouse = await firstValueFrom(this.api.createWarehouse(req));
    this.warehouses.update(list => [...list, warehouse]);
    this.notify.success(`Warehouse "${warehouse.name}" created`);
    return warehouse;
  }
}
