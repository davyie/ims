import { Injectable, inject, signal } from '@angular/core';
import { Transaction } from '../../../shared/models/models';
import { TransactionApiService } from './transaction-api.service';

@Injectable({ providedIn: 'root' })
export class TransactionStateService {
  private api = inject(TransactionApiService);

  readonly transactions = signal<Transaction[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadAllTransactions(): void {
    this.loading.set(true);
    this.api.getAllTransactions().subscribe({
      next: txns => { this.transactions.set(txns); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  loadItemTransactions(itemId: string): void {
    this.loading.set(true);
    this.api.getItemTransactions(itemId).subscribe({
      next: txns => { this.transactions.set(txns); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }

  loadMarketItemTransactions(marketId: string, itemId: string): void {
    this.loading.set(true);
    this.api.getMarketItemTransactions(marketId, itemId).subscribe({
      next: txns => { this.transactions.set(txns); this.loading.set(false); },
      error: err => { this.error.set(err.message); this.loading.set(false); }
    });
  }
}
