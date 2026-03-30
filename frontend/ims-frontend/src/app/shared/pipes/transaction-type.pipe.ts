import { Pipe, PipeTransform } from '@angular/core';
import { TransactionType } from '../models/models';

@Pipe({ name: 'transactionType', standalone: true })
export class TransactionTypePipe implements PipeTransform {
  transform(type: TransactionType): string {
    const labels: Record<TransactionType, string> = {
      SHIFT_TO_MARKET: 'Shifted to Market',
      SALE: 'Sale',
      RETURN_FROM_MARKET: 'Return from Market',
      STOCK_ADJUSTMENT: 'Stock Adjustment',
      INCREMENT: 'Stock Increment',
    };
    return labels[type] ?? type;
  }
}
