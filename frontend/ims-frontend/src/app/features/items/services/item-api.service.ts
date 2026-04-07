import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Item, Page, CreateItemRequest, UpdateItemRequest } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ItemApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/items`;

  getItems(page = 0, size = 100): Observable<Page<Item>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Item>>(this.base, { params });
  }

  getItem(itemId: string): Observable<Item> {
    return this.http.get<Item>(`${this.base}/${itemId}`);
  }

  createItem(req: CreateItemRequest): Observable<Item> {
    return this.http.post<Item>(this.base, req);
  }

  updateItem(itemId: string, req: UpdateItemRequest): Observable<Item> {
    return this.http.put<Item>(`${this.base}/${itemId}`, req);
  }

  deleteItem(itemId: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${itemId}`);
  }
}
