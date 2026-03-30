import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stock-level',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="stock-display">
      <span class="stock-value">{{ stock }}</span>
      <span *ngIf="level !== 'none'" class="stock-badge" [ngClass]="'stock-' + level">
        {{ levelLabel }}
      </span>
    </span>
  `,
  styles: [`
    .stock-display { display: inline-flex; align-items: center; gap: 6px; }
    .stock-value { font-weight: 500; }
    .stock-badge {
      font-size: 10px;
      font-weight: 700;
      padding: 1px 7px;
      border-radius: 8px;
      text-transform: uppercase;
    }
    .stock-critical { background: #FFEBEE; color: #C62828; }
    .stock-low      { background: #FFF8E1; color: #F57F17; }
    .stock-ok       { background: #F3E5F5; color: #6A1B9A; }
    .stock-well     { background: #E8F5E9; color: #2E7D32; }
  `]
})
export class StockLevelComponent {
  @Input() stock: number = 0;

  get level(): 'critical' | 'low' | 'ok' | 'well' | 'none' {
    if (this.stock === 0) return 'critical';
    if (this.stock <= 5) return 'low';
    if (this.stock <= 20) return 'ok';
    return 'none';
  }

  get levelLabel(): string {
    const labels: Record<string, string> = {
      critical: 'Out',
      low: 'Low',
      ok: 'OK',
    };
    return labels[this.level] ?? '';
  }
}
