import { Pipe, PipeTransform } from '@angular/core';

export type StockLevel = 'critical' | 'low' | 'ok' | 'well-stocked';

@Pipe({ name: 'stockLevel', standalone: true })
export class StockLevelPipe implements PipeTransform {
  transform(stock: number): StockLevel {
    if (stock === 0) return 'critical';
    if (stock <= 5) return 'low';
    if (stock <= 20) return 'ok';
    return 'well-stocked';
  }
}
