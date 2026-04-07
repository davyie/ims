import { Injectable, inject, signal } from '@angular/core';
import { Item, CreateItemRequest, UpdateItemRequest } from '../../../shared/models/models';
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

  loadItems(): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getItems().subscribe({
      next: page => { this.items.set(page.content); this.loading.set(false); },
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

  createItem(cmd: CreateItemRequest): Promise<Item> {
    return new Promise((resolve, reject) => {
      this.api.createItem(cmd).subscribe({
        next: item => {
          this.items.update(items => [...items, item]);
          this.notify.success(`Item "${item.name}" created`);
          resolve(item);
        },
        error: err => reject(err)
      });
    });
  }

  updateItem(id: string, cmd: UpdateItemRequest): Promise<Item> {
    return new Promise((resolve, reject) => {
      this.api.updateItem(id, cmd).subscribe({
        next: item => {
          this.items.update(items => items.map(i => i.itemId === id ? item : i));
          this.selectedItem.set(item);
          this.notify.success(`Item "${item.name}" updated`);
          resolve(item);
        },
        error: err => reject(err)
      });
    });
  }

  deleteItem(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.api.deleteItem(id).subscribe({
        next: () => {
          this.items.update(items => items.filter(i => i.itemId !== id));
          if (this.selectedItem()?.itemId === id) this.selectedItem.set(null);
          this.notify.success('Item deleted');
          resolve();
        },
        error: err => reject(err)
      });
    });
  }
}
