import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { TransactionStateService } from '../../services/transaction-state.service';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { TransactionRecord } from '../../../../shared/models/models';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule,
    MatCardModule, MatTableModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatPaginatorModule,
    DateFormatPipe, PageHeaderComponent
  ],
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.scss']
})
export class TransactionListComponent implements OnInit {
  txnState = inject(TransactionStateService);

  selectedService = signal<string>('');
  selectedEventType = signal<string>('');
  pageIndex = signal(0);
  pageSize = signal(20);

  columns = ['occurred', 'eventType', 'service', 'entityId', 'userId'];

  filteredTxns = computed(() => {
    let txns = this.txnState.transactions();
    const svc = this.selectedService();
    const type = this.selectedEventType();
    if (svc) txns = txns.filter(t => t.originService === svc);
    if (type) txns = txns.filter(t => t.eventType === type);
    return txns;
  });

  pagedTxns = computed(() => {
    const start = this.pageIndex() * this.pageSize();
    return this.filteredTxns().slice(start, start + this.pageSize());
  });

  uniqueServices = computed(() =>
    [...new Set(this.txnState.transactions().map(t => t.originService))].filter(Boolean)
  );

  uniqueEventTypes = computed(() =>
    [...new Set(this.txnState.transactions().map(t => t.eventType))].filter(Boolean)
  );

  ngOnInit(): void {
    this.txnState.loadTransactions({ size: 100 });
  }

  onPage(e: PageEvent): void {
    this.pageIndex.set(e.pageIndex);
    this.pageSize.set(e.pageSize);
  }

  exportCsv(): void {
    const txns = this.filteredTxns();
    const header = 'Date,Event Type,Service,Entity ID,User ID\n';
    const rows = txns.map((t: TransactionRecord) =>
      `"${t.occurredAt ?? t.recordedAt}","${t.eventType}","${t.originService}","${t.entityId ?? ''}","${t.userId ?? ''}"`
    ).join('\n');
    const blob = new Blob([header + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'transactions.csv'; a.click();
    URL.revokeObjectURL(url);
  }
}
