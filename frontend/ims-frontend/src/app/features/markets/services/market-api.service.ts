import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Market, MarketSummary, AllMarketsSummary, CreateMarketRequest } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class MarketApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/markets`;

  getMarkets(status?: string): Observable<Market[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<Market[]>(this.base, { params });
  }

  getMarket(id: string): Observable<Market> {
    return this.http.get<Market>(`${this.base}/${id}`);
  }

  createMarket(req: CreateMarketRequest): Observable<Market> {
    return this.http.post<Market>(this.base, req);
  }

  openMarket(id: string): Observable<Market> {
    return this.http.post<Market>(`${this.base}/${id}/open`, {});
  }

  closeMarket(id: string, createdBy?: string): Observable<Market> {
    let params = new HttpParams();
    if (createdBy) params = params.set('createdBy', createdBy);
    return this.http.post<Market>(`${this.base}/${id}/close`, {}, { params });
  }

  getMarketSummary(id: string): Observable<MarketSummary> {
    return this.http.get<MarketSummary>(`${this.base}/${id}/summary`);
  }

  getAllMarketsSummary(status?: string): Observable<AllMarketsSummary> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<AllMarketsSummary>(`${this.base}/summary`, { params });
  }
}
