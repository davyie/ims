import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Category } from '../../../shared/models/models';

// Categories are managed locally — the backend has no category service.
// Items store category as a free-text string; this list provides the dropdown options.
const PREDEFINED_CATEGORIES: Category[] = [
  { id: '1', name: 'Food & Beverage' },
  { id: '2', name: 'Clothing & Accessories' },
  { id: '3', name: 'Crafts & Handmade' },
  { id: '4', name: 'Plants & Garden' },
  { id: '5', name: 'Home & Kitchen' },
  { id: '6', name: 'Health & Beauty' },
  { id: '7', name: 'Books & Media' },
  { id: '8', name: 'Tools & Hardware' },
  { id: '9', name: 'Toys & Games' },
  { id: '10', name: 'Other' },
];

@Injectable({ providedIn: 'root' })
export class CategoryApiService {
  getCategories(): Observable<Category[]> {
    return of(PREDEFINED_CATEGORIES);
  }

  createCategory(req: { name: string }): Observable<Category> {
    const cat: Category = { id: Date.now().toString(), name: req.name };
    return of(cat);
  }

  deleteCategory(_id: string): Observable<void> {
    return of(undefined as void);
  }
}
