import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'transactionType', standalone: true })
export class TransactionTypePipe implements PipeTransform {
  transform(type: string): string {
    const labels: Record<string, string> = {
      SHIFT_TO_MARKET: 'Shifted to Market',
      SALE: 'Sale',
      RETURN_FROM_MARKET: 'Return from Market',
      STOCK_ADJUSTMENT: 'Stock Adjustment',
      INCREMENT: 'Stock Increment',
    };
    return labels[type] ?? type;
  }
}
