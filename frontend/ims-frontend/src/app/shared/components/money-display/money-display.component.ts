import { Component, Input } from '@angular/core';
import { CurrencyFormatPipe } from '../../pipes/currency-format.pipe';

@Component({
  selector: 'app-money-display',
  standalone: true,
  imports: [CurrencyFormatPipe],
  template: `<span class="money">{{ amount | currencyFormat:currency }}</span>`,
  styles: [`.money { font-variant-numeric: tabular-nums; }`]
})
export class MoneyDisplayComponent {
  @Input() amount: number = 0;
  @Input() currency: string = 'SEK';
}
