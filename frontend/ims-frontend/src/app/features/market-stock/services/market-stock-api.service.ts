import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MarketStock, Page, StockOperationRequest, Transfer, CreateTransferRequest } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class MarketStockApiService {
  private http = inject(HttpClient);
  private marketsBase = `${environment.apiBaseUrl}/markets`;
  private transfersBase = `${environment.apiBaseUrl}/transfers`;

  getMarketStock(marketId: string, page = 0, size = 100): Observable<Page<MarketStock>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<MarketStock>>(`${this.marketsBase}/${marketId}/stock`, { params });
  }

  /** Shift items from a warehouse to a market via the transfer service. */
  shiftToMarket(req: CreateTransferRequest): Observable<Transfer> {
    return this.http.post<Transfer>(this.transfersBase, req);
  }

  incrementStock(marketId: string, req: StockOperationRequest): Observable<MarketStock> {
    return this.http.post<MarketStock>(`${this.marketsBase}/${marketId}/stock/increment`, req);
  }

  decrementStock(marketId: string, req: StockOperationRequest): Observable<MarketStock> {
    return this.http.post<MarketStock>(`${this.marketsBase}/${marketId}/stock/decrement`, req);
  }
}
