import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MarketSummary, AllMarketsSummary } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ReportApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/markets`;

  getMarketSummary(id: string): Observable<MarketSummary> {
    return this.http.get<MarketSummary>(`${this.base}/${id}/summary`);
  }

  getAllMarketsSummary(status?: string): Observable<AllMarketsSummary> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<AllMarketsSummary>(`${this.base}/summary`, { params });
  }
}
