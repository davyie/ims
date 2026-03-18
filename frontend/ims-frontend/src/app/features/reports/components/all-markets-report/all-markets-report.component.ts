import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { ReportStateService } from '../../services/report-state.service';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-all-markets-report',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatSortModule,
    MatFormFieldModule, MatSelectModule,
    BaseChartDirective,
    CurrencyFormatPipe, PageHeaderComponent
  ],
  templateUrl: './all-markets-report.component.html',
  styleUrls: ['./all-markets-report.component.scss']
})
export class AllMarketsReportComponent implements OnInit {
  private fb = inject(FormBuilder);
  state = inject(ReportStateService);

  selectedStatus = signal<string>('');
  marketColumns = ['name', 'itemTypes', 'allocated', 'sold', 'revenue'];

  filterForm = this.fb.group({ status: [''] });

  get barChartData(): ChartConfiguration<'bar'>['data'] {
    const s = this.state.allSummary();
    if (!s) return { labels: [], datasets: [] };
    return {
      labels: s.markets.map(m => m.marketName),
      datasets: [{
        data: s.markets.map(m => m.totalRevenue),
        label: 'Revenue',
        backgroundColor: s.markets.map((_, i) => [
          '#1B3A6B', '#C0392B', '#2E7D32', '#F57F17', '#6A1B9A'
        ][i % 5]),
      }]
    };
  }

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true } }
  };

  ngOnInit(): void {
    this.state.loadAllSummary();
  }

  applyFilter(): void {
    const status = this.filterForm.get('status')?.value || undefined;
    this.state.loadAllSummary(status || undefined);
  }

  exportCsv(): void {
    const s = this.state.allSummary();
    if (!s) return;
    const header = 'Market,Item Types,Allocated,Sold,Revenue\n';
    const rows = s.markets.map(m =>
      `"${m.marketName}",${m.totalItemTypes},${m.totalAllocatedStock},${m.totalSold},${m.totalRevenue}`
    ).join('\n');
    const blob = new Blob([header + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'all-markets-report.csv'; a.click();
    URL.revokeObjectURL(url);
  }
}
