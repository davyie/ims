import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  MarketItem, Transaction,
  ShiftItemRequest, IncrementStockRequest, DecrementStockRequest, SetPriceRequest
} from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class MarketStockApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/markets`;

  getMarketItems(marketId: string): Observable<MarketItem[]> {
    return this.http.get<MarketItem[]>(`${this.base}/${marketId}/items`);
  }

  shiftItem(marketId: string, req: ShiftItemRequest): Observable<MarketItem> {
    return this.http.post<MarketItem>(`${this.base}/${marketId}/items`, req);
  }

  getMarketItem(marketId: string, itemId: string): Observable<MarketItem> {
    return this.http.get<MarketItem>(`${this.base}/${marketId}/items/${itemId}`);
  }

  incrementStock(marketId: string, itemId: string, req: IncrementStockRequest): Observable<MarketItem> {
    return this.http.patch<MarketItem>(`${this.base}/${marketId}/items/${itemId}/increment`, req);
  }

  decrementStock(marketId: string, itemId: string, req: DecrementStockRequest): Observable<MarketItem> {
    return this.http.patch<MarketItem>(`${this.base}/${marketId}/items/${itemId}/decrement`, req);
  }

  setMarketItemPrice(marketId: string, itemId: string, req: SetPriceRequest): Observable<MarketItem> {
    return this.http.put<MarketItem>(`${this.base}/${marketId}/items/${itemId}/price`, req);
  }

  getMarketItemTransactions(marketId: string, itemId: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.base}/${marketId}/items/${itemId}/transactions`);
  }
}
