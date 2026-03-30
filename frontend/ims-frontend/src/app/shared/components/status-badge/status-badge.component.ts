import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule, MatChipsModule],
  template: `
    <span class="status-badge" [ngClass]="badgeClass">{{ label }}</span>
  `,
  styles: [`
    .status-badge {
      display: inline-block;
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
      letter-spacing: 0.3px;
      text-transform: uppercase;
    }
    .badge-scheduled { background: #E3F2FD; color: #1565C0; }
    .badge-open      { background: #E8F5E9; color: #2E7D32; }
    .badge-closed    { background: #FAFAFA; color: #616161; border: 1px solid #E0E0E0; }
    .badge-sale             { background: #E8F5E9; color: #2E7D32; }
    .badge-shift_to_market  { background: #E3F2FD; color: #1565C0; }
    .badge-return_from_market { background: #FFF3E0; color: #E65100; }
    .badge-stock_adjustment { background: #F3E5F5; color: #6A1B9A; }
    .badge-increment        { background: #E0F2F1; color: #00695C; }
    .badge-default          { background: #F5F5F5; color: #424242; }
  `]
})
export class StatusBadgeComponent {
  @Input() status: string = '';
  @Input() type: 'market' | 'transaction' = 'market';

  get badgeClass(): string {
    return `badge-${this.status.toLowerCase()}`;
  }

  get label(): string {
    const labels: Record<string, string> = {
      SCHEDULED: 'Scheduled',
      OPEN: 'Open',
      CLOSED: 'Closed',
      SALE: 'Sale',
      SHIFT_TO_MARKET: 'Shifted',
      RETURN_FROM_MARKET: 'Returned',
      STOCK_ADJUSTMENT: 'Adjusted',
      INCREMENT: 'Incremented',
    };
    return labels[this.status] ?? this.status;
  }
}
