import { Injectable, inject, signal } from '@angular/core';
import { TransactionRecord } from '../../../shared/models/models';
import { TransactionApiService } from './transaction-api.service';

@Injectable({ providedIn: 'root' })
export class TransactionStateService {
  private api = inject(TransactionApiService);

  readonly transactions = signal<TransactionRecord[]>([]);
  readonly totalElements = signal(0);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadTransactions(options: { userId?: string; service?: string; eventType?: string; page?: number; size?: number } = {}): void {
    this.loading.set(true);
    this.api.getTransactions(options).subscribe({
      next: page => {
        this.transactions.set(page.content);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
