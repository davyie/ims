import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ReportStateService } from '../../services/report-state.service';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EventProjectionDocument } from '../../../../shared/models/models';

@Component({
  selector: 'app-all-markets-report',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule,
    MatFormFieldModule, MatSelectModule,
    DateFormatPipe, PageHeaderComponent
  ],
  templateUrl: './all-markets-report.component.html',
  styleUrls: ['./all-markets-report.component.scss']
})
export class AllMarketsReportComponent implements OnInit {
  state = inject(ReportStateService);

  eventColumns = ['occurred', 'eventType', 'service', 'entityId'];

  ngOnInit(): void {
    this.state.loadTransferReport();
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
    a.href = url; a.download = 'transfers-report.csv'; a.click();
    URL.revokeObjectURL(url);
  }
}
