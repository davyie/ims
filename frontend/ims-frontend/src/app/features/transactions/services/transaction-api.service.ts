import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { TransactionRecord, Page } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class TransactionApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/transactions`;

  getTransactions(options: {
    userId?: string;
    service?: string;
    eventType?: string;
    page?: number;
    size?: number;
  } = {}): Observable<Page<TransactionRecord>> {
    let params = new HttpParams()
      .set('page', options.page ?? 0)
      .set('size', options.size ?? 50);
    if (options.userId) params = params.set('userId', options.userId);
    if (options.service) params = params.set('service', options.service);
    if (options.eventType) params = params.set('eventType', options.eventType);
    return this.http.get<Page<TransactionRecord>>(this.base, { params });
  }

  getCorrelationChain(correlationId: string): Observable<TransactionRecord[]> {
    return this.http.get<TransactionRecord[]>(`${this.base}/${correlationId}/chain`);
  }
}
