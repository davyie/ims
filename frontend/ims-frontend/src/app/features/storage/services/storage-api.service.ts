import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { StorageSummary } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class StorageApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/storage`;

  getStorageSummary(): Observable<StorageSummary> {
    return this.http.get<StorageSummary>(`${this.base}/summary`);
  }
}
