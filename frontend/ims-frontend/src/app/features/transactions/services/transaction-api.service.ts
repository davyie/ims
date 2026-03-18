import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Transaction } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class TransactionApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/transactions`;
  private itemsBase = `${environment.apiBaseUrl}/items`;
  private marketsBase = `${environment.apiBaseUrl}/markets`;

  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.base);
  }

  getItemTransactions(itemId: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.itemsBase}/${itemId}/transactions`);
  }

  getMarketItemTransactions(marketId: string, itemId: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.marketsBase}/${marketId}/items/${itemId}/transactions`);
  }
}
