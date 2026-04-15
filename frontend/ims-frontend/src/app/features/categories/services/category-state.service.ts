import { Injectable, inject, signal } from '@angular/core';
import { Category } from '../../../shared/models/models';
import { CategoryApiService } from './category-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable({ providedIn: 'root' })
export class CategoryStateService {
  private api = inject(CategoryApiService);
  private notify = inject(NotificationService);

  readonly categories = signal<Category[]>([]);
  readonly loading = signal(false);

  loadCategories(): void {
    this.loading.set(true);
    this.api.getCategories().subscribe({
      next: cats => { this.categories.set(cats); this.loading.set(false); },
      error: () => { this.loading.set(false); }
    });
  }

  createCategory(req: { name: string }): Promise<Category> {
    return new Promise((resolve, reject) => {
      this.api.createCategory(req).subscribe({
        next: cat => {
          this.categories.update(cats => [...cats, cat]);
          this.notify.success(`Category "${cat.name}" created`);
          resolve(cat);
        },
        error: err => reject(err)
      });
    });
  }

  deleteCategory(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.api.deleteCategory(id).subscribe({
        next: () => {
          this.categories.update(cats => cats.filter(c => c.id !== id));
          this.notify.success('Category removed');
          resolve();
        },
        error: err => reject(err)
      });
    });
  }
}
