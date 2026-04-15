import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ReportApiService } from '../../services/report-api.service';
import { MarketApiService } from '../../../markets/services/market-api.service';
import { ItemStateService } from '../../../items/services/item-state.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { Market, EventProjectionDocument } from '../../../../shared/models/models';

interface ItemStat {
  itemId: string;
  unitsSold: number;
  transactions: number;
}

interface MarketStat {
  market: Market;
  totalUnitsSold: number;
  totalTransactions: number;
  topItems: ItemStat[];
}

@Component({
  selector: 'app-all-markets-report',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatProgressSpinnerModule, MatTooltipModule,
    StatusBadgeComponent, PageHeaderComponent
  ],
  templateUrl: './all-markets-report.component.html',
  styleUrls: ['./all-markets-report.component.scss']
})
export class AllMarketsReportComponent implements OnInit {
  private reportApi  = inject(ReportApiService);
  private marketApi  = inject(MarketApiService);
  private itemState  = inject(ItemStateService);

  readonly loading = signal(false);
  readonly stats   = signal<MarketStat[]>([]);

  readonly totalUnitsSold    = computed(() => this.stats().reduce((s, m) => s + m.totalUnitsSold, 0));
  readonly totalTransactions = computed(() => this.stats().reduce((s, m) => s + m.totalTransactions, 0));
  readonly topMarket         = computed(() =>
    [...this.stats()].sort((a, b) => b.totalUnitsSold - a.totalUnitsSold)[0] ?? null
  );
  readonly maxUnits = computed(() =>
    Math.max(1, ...this.stats().map(m => m.totalUnitsSold))
  );

  columns = ['market', 'status', 'transactions', 'units', 'bar', 'top-item'];

  ngOnInit(): void {
    this.loading.set(true);
    this.itemState.loadItems();

    forkJoin({
      markets: this.marketApi.getMarkets(0, 200),
      sales:   this.reportApi.getMarketSalesReport()
    }).subscribe({
      next: ({ markets, sales }) => {
        const marketMap = new Map<string, Market>(
          markets.content.map(m => [m.marketId, m])
        );

        // Group sale events by marketId
        const byMarket = new Map<string, EventProjectionDocument[]>();
        for (const event of sales.content) {
          const marketId = event.entityId ?? (event.payload?.['marketId'] as string);
          if (!marketId) continue;
          if (!byMarket.has(marketId)) byMarket.set(marketId, []);
          byMarket.get(marketId)!.push(event);
        }

        // Build stats for every market (including those with 0 sales)
        const result: MarketStat[] = markets.content.map(m => {
          const events = byMarket.get(m.marketId) ?? [];
          const itemMap = new Map<string, ItemStat>();

          for (const ev of events) {
            const itemId  = ev.payload?.['itemId'] as string;
            const qty     = Number(ev.payload?.['quantity'] ?? 1);
            if (!itemId) continue;
            const existing = itemMap.get(itemId) ?? { itemId, unitsSold: 0, transactions: 0 };
            existing.unitsSold   += qty;
            existing.transactions += 1;
            itemMap.set(itemId, existing);
          }

          const topItems = [...itemMap.values()]
            .sort((a, b) => b.unitsSold - a.unitsSold)
            .slice(0, 5);

          return {
            market: m,
            totalUnitsSold:    topItems.reduce((s, i) => s + i.unitsSold, 0),
            totalTransactions: events.length,
            topItems
          };
        });

        // Sort by units sold descending
        result.sort((a, b) => b.totalUnitsSold - a.totalUnitsSold);
        this.stats.set(result);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  getItemName(itemId: string): string {
    return this.itemState.items().find(i => i.itemId === itemId)?.name
      ?? itemId.slice(0, 8) + '…';
  }

  barWidth(units: number): string {
    return (units / this.maxUnits() * 100).toFixed(1) + '%';
  }

  exportCsv(): void {
    const rows = this.stats();
    if (!rows.length) return;
    const header = 'Market,Status,Type,Sale Transactions,Units Sold\n';
    const body = rows.map(r =>
      `"${r.market.name}","${r.market.status}","${r.market.marketType}",${r.totalTransactions},${r.totalUnitsSold}`
    ).join('\n');
    const blob = new Blob([header + body], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'market-sales-report.csv'; a.click();
    URL.revokeObjectURL(url);
  }
}
