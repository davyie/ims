import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Market, MarketStock, Page, CreateMarketRequest, UpdateMarketRequest, StockOperationRequest } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class MarketApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/markets`;

  getMarkets(page = 0, size = 100): Observable<Page<Market>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Market>>(this.base, { params });
  }

  getMarket(marketId: string): Observable<Market> {
    return this.http.get<Market>(`${this.base}/${marketId}`);
  }

  createMarket(req: CreateMarketRequest): Observable<Market> {
    return this.http.post<Market>(this.base, req);
  }

  updateMarket(marketId: string, req: UpdateMarketRequest): Observable<Market> {
    return this.http.put<Market>(`${this.base}/${marketId}`, req);
  }

  deleteMarket(marketId: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${marketId}`);
  }

  openMarket(marketId: string): Observable<Market> {
    return this.http.post<Market>(`${this.base}/${marketId}/open`, {});
  }

  closeMarket(marketId: string): Observable<Market> {
    return this.http.post<Market>(`${this.base}/${marketId}/close`, {});
  }

  getMarketStock(marketId: string, page = 0, size = 100): Observable<Page<MarketStock>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<MarketStock>>(`${this.base}/${marketId}/stock`, { params });
  }

  incrementStock(marketId: string, req: StockOperationRequest): Observable<MarketStock> {
    return this.http.post<MarketStock>(`${this.base}/${marketId}/stock/increment`, req);
  }

  decrementStock(marketId: string, req: StockOperationRequest): Observable<MarketStock> {
    return this.http.post<MarketStock>(`${this.base}/${marketId}/stock/decrement`, req);
  }
}
