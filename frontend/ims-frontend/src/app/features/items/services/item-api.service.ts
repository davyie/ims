import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  Item, Transaction,
  RegisterItemRequest, UpdateItemRequest, AdjustStockRequest
} from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ItemApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/items`;

  getItems(category?: string): Observable<Item[]> {
    let params = new HttpParams();
    if (category) params = params.set('category', category);
    return this.http.get<Item[]>(this.base, { params });
  }

  getItem(id: string): Observable<Item> {
    return this.http.get<Item>(`${this.base}/${id}`);
  }

  registerItem(req: RegisterItemRequest): Observable<Item> {
    return this.http.post<Item>(this.base, req);
  }

  updateItem(id: string, req: UpdateItemRequest): Observable<Item> {
    return this.http.put<Item>(`${this.base}/${id}`, req);
  }

  adjustStock(id: string, req: AdjustStockRequest): Observable<Item> {
    return this.http.patch<Item>(`${this.base}/${id}/stock`, req);
  }

  getItemTransactions(id: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.base}/${id}/transactions`);
  }
}
