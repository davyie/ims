import { Injectable, inject, signal } from '@angular/core';
import { Category, CreateCategoryRequest } from '../../../shared/models/models';
import { CategoryApiService } from './category-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable({ providedIn: 'root' })
export class CategoryStateService {
  private api = inject(CategoryApiService);
  private notify = inject(NotificationService);

  readonly categories = signal<Category[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadCategories(): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getCategories().subscribe({
      next: cats => { this.categories.set(cats); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  createCategory(req: CreateCategoryRequest): Promise<Category> {
    return new Promise((resolve, reject) => {
      this.api.createCategory(req).subscribe({
        next: cat => {
          this.categories.update(cats => [...cats, cat]);
          this.notify.success(`Category "${cat.name}" created`);
          resolve(cat);
        },
        error: err => { reject(err); }
      });
    });
  }

  deleteCategory(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.api.deleteCategory(id).subscribe({
        next: () => {
          this.categories.update(cats => cats.filter(c => c.id !== id));
          this.notify.success('Category deleted');
          resolve();
        },
        error: err => { reject(err); }
      });
    });
  }
}
