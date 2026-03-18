import { Injectable, inject, signal, computed } from '@angular/core';
import { StorageItem } from '../../../shared/models/models';
import { StorageApiService } from './storage-api.service';

@Injectable({ providedIn: 'root' })
export class StorageStateService {
  private api = inject(StorageApiService);

  readonly storageItems = signal<StorageItem[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly lowStockItems = computed(() => this.storageItems().filter(i => i.currentStock === 0));

  loadStorageItems(): void {
    this.loading.set(true);
    this.api.getStorageSummary().subscribe({
      next: s => { this.storageItems.set(s.items); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
