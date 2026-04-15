import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EventProjectionDocument, Page } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ReportApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/reports`;

  getMarketReport(marketId: string, type: 'SESSION' | 'STOCK_COMPARISON' = 'SESSION', page = 0, size = 50): Observable<Page<EventProjectionDocument>> {
    const params = new HttpParams().set('type', type).set('page', page).set('size', size);
    return this.http.get<Page<EventProjectionDocument>>(`${this.base}/market/${marketId}`, { params });
  }

  getWarehouseReport(warehouseId: string, type: 'INVENTORY_SNAPSHOT' | 'MOVEMENT_HISTORY' | 'LOW_STOCK' | 'VALUATION' = 'INVENTORY_SNAPSHOT', page = 0, size = 50): Observable<Page<EventProjectionDocument>> {
    const params = new HttpParams().set('type', type).set('page', page).set('size', size);
    return this.http.get<Page<EventProjectionDocument>>(`${this.base}/warehouse/${warehouseId}`, { params });
  }

  getMarketSalesReport(page = 0, size = 1000): Observable<Page<EventProjectionDocument>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<EventProjectionDocument>>(`${this.base}/markets/sales`, { params });
  }

  getTransferReport(page = 0, size = 50): Observable<Page<EventProjectionDocument>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<EventProjectionDocument>>(`${this.base}/transfers`, { params });
  }
}
