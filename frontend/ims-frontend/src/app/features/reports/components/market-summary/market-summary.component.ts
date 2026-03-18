import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { ReportStateService } from '../../services/report-state.service';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-market-summary',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatCardModule, MatButtonModule, MatIconModule, MatTableModule,
    BaseChartDirective,
    CurrencyFormatPipe, PageHeaderComponent
  ],
  templateUrl: './market-summary.component.html',
  styleUrls: ['./market-summary.component.scss']
})
export class MarketSummaryComponent implements OnInit {
  private route = inject(ActivatedRoute);
  state = inject(ReportStateService);

  itemColumns = ['sku', 'name', 'allocated', 'current', 'sold', 'revenue'];

  get id(): string { return this.route.snapshot.paramMap.get('id')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Markets', link: '/markets' },
      { label: this.state.marketSummary()?.marketName ?? 'Market', link: `/markets/${this.id}` },
      { label: 'Summary' }
    ];
  }

  get barChartData(): ChartConfiguration<'bar'>['data'] {
    const s = this.state.marketSummary();
    if (!s) return { labels: [], datasets: [] };
    return {
      labels: s.items.map(i => i.itemName),
      datasets: [{
        data: s.items.map(i => i.revenue),
        label: 'Revenue',
        backgroundColor: '#1B3A6B',
      }]
    };
  }

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true } }
  };

  ngOnInit(): void {
    this.state.loadMarketSummary(this.id);
  }

  exportCsv(): void {
    const s = this.state.marketSummary();
    if (!s) return;
    const header = 'SKU,Name,Allocated,Current,Sold,Revenue\n';
    const rows = s.items.map(i =>
      `"${i.sku}","${i.itemName}",${i.allocatedStock},${i.currentStock},${i.sold},${i.revenue}`
    ).join('\n');
    const blob = new Blob([header + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = `${s.marketName}-summary.csv`; a.click();
    URL.revokeObjectURL(url);
  }
}
