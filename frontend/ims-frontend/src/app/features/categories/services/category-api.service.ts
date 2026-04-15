import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Category } from '../../../shared/models/models';

const STORAGE_KEY = 'ims_categories';

function loadFromStorage(): Category[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

function saveToStorage(cats: Category[]): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(cats));
}

@Injectable({ providedIn: 'root' })
export class CategoryApiService {

  getCategories(): Observable<Category[]> {
    return of(loadFromStorage());
  }

  createCategory(req: { name: string }): Observable<Category> {
    const cats = loadFromStorage();
    const existing = cats.find(c => c.name.toLowerCase() === req.name.toLowerCase());
    if (existing) return of(existing);
    const cat: Category = { id: Date.now().toString(), name: req.name };
    saveToStorage([...cats, cat]);
    return of(cat);
  }

  deleteCategory(id: string): Observable<void> {
    const cats = loadFromStorage().filter(c => c.id !== id);
    saveToStorage(cats);
    return of(undefined as void);
  }

  /** Adds categories from item data that are not already stored. */
  seedFromItems(names: string[]): void {
    const cats = loadFromStorage();
    const existing = new Set(cats.map(c => c.name.toLowerCase()));
    const toAdd = names
      .filter(n => n && !existing.has(n.toLowerCase()))
      .map(n => ({ id: Date.now().toString() + Math.random(), name: n }));
    if (toAdd.length > 0) saveToStorage([...cats, ...toAdd]);
  }
}
