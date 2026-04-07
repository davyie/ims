import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { ReportStateService } from '../../services/report-state.service';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { EventProjectionDocument } from '../../../../shared/models/models';

@Component({
  selector: 'app-market-summary',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatCardModule, MatButtonModule, MatIconModule, MatTableModule,
    DateFormatPipe, PageHeaderComponent
  ],
  templateUrl: './market-summary.component.html',
  styleUrls: ['./market-summary.component.scss']
})
export class MarketSummaryComponent implements OnInit {
  private route = inject(ActivatedRoute);
  state = inject(ReportStateService);

  eventColumns = ['occurred', 'eventType', 'service', 'entityId'];

  get id(): string { return this.route.snapshot.paramMap.get('id')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Markets', link: '/markets' },
      { label: 'Market', link: `/markets/${this.id}` },
      { label: 'Summary' }
    ];
  }

  ngOnInit(): void {
    this.state.loadMarketReport(this.id);
  }

  exportCsv(): void {
    const events = this.state.events();
    if (!events.length) return;
    const header = 'Date,Event Type,Service,Entity ID\n';
    const rows = events.map((e: EventProjectionDocument) =>
      `"${e.occurredAt ?? e.recordedAt}","${e.eventType}","${e.originService}","${e.entityId ?? ''}"`
    ).join('\n');
    const blob = new Blob([header + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = `market-${this.id}-report.csv`; a.click();
    URL.revokeObjectURL(url);
  }
}
