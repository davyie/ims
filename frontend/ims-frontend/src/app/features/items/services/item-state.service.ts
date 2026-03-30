import { Injectable, inject, signal } from '@angular/core';
import { Item, RegisterItemRequest, UpdateItemRequest, AdjustStockRequest } from '../../../shared/models/models';
import { ItemApiService } from './item-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable({ providedIn: 'root' })
export class ItemStateService {
  private api = inject(ItemApiService);
  private notify = inject(NotificationService);

  readonly items = signal<Item[]>([]);
  readonly selectedItem = signal<Item | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadItems(category?: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getItems(category).subscribe({
      next: items => { this.items.set(items); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  loadItem(id: string): void {
    this.loading.set(true);
    this.api.getItem(id).subscribe({
      next: item => { this.selectedItem.set(item); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  registerItem(cmd: RegisterItemRequest): Promise<Item> {
    return new Promise((resolve, reject) => {
      this.api.registerItem(cmd).subscribe({
        next: item => {
          this.items.update(items => [...items, item]);
          this.notify.success(`Item "${item.name}" registered`);
          resolve(item);
        },
        error: err => { reject(err); }
      });
    });
  }

  updateItem(id: string, cmd: UpdateItemRequest): Promise<Item> {
    return new Promise((resolve, reject) => {
      this.api.updateItem(id, cmd).subscribe({
        next: item => {
          this.items.update(items => items.map(i => i.id === id ? item : i));
          this.selectedItem.set(item);
          this.notify.success(`Item "${item.name}" updated`);
          resolve(item);
        },
        error: err => { reject(err); }
      });
    });
  }

  deleteItem(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.api.deleteItem(id).subscribe({
        next: () => {
          this.items.update(items => items.filter(i => i.id !== id));
          if (this.selectedItem()?.id === id) this.selectedItem.set(null);
          this.notify.success('Item deleted');
          resolve();
        },
        error: err => { reject(err); }
      });
    });
  }

  adjustStock(id: string, cmd: AdjustStockRequest): Promise<Item> {
    return new Promise((resolve, reject) => {
      this.api.adjustStock(id, cmd).subscribe({
        next: item => {
          this.items.update(items => items.map(i => i.id === id ? item : i));
          this.selectedItem.set(item);
          this.notify.success('Stock adjusted');
          resolve(item);
        },
        error: err => { reject(err); }
      });
    });
  }
}
