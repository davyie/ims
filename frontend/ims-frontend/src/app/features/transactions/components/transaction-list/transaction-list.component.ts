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
import { MatChipsModule } from '@angular/material/chips';
import { TransactionStateService } from '../../services/transaction-state.service';
import { ItemStateService } from '../../../items/services/item-state.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { TransactionTypePipe } from '../../../../shared/pipes/transaction-type.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { Transaction, TransactionType } from '../../../../shared/models/models';

const TXN_TYPES: TransactionType[] = [
  'SHIFT_TO_MARKET', 'SALE', 'RETURN_FROM_MARKET', 'STOCK_ADJUSTMENT', 'INCREMENT'
];

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule,
    MatCardModule, MatTableModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatPaginatorModule, MatChipsModule,
    StatusBadgeComponent, CurrencyFormatPipe, DateFormatPipe, TransactionTypePipe, PageHeaderComponent
  ],
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.scss']
})
export class TransactionListComponent implements OnInit {
  txnState = inject(TransactionStateService);
  itemState = inject(ItemStateService);

  selectedItemId = signal<string>('');
  selectedType = signal<string>('');
  pageIndex = signal(0);
  pageSize = signal(20);

  txnTypes = TXN_TYPES;

  columns = ['occurred', 'type', 'item', 'delta', 'before', 'after', 'note', 'createdBy'];

  filteredTxns = computed(() => {
    let txns = this.txnState.transactions();
    const type = this.selectedType();
    const itemId = this.selectedItemId();
    if (type) txns = txns.filter(t => t.type === type);
    if (itemId) txns = txns.filter(t => t.itemId === itemId);
    return txns;
  });

  pagedTxns = computed(() => {
    const start = this.pageIndex() * this.pageSize();
    return this.filteredTxns().slice(start, start + this.pageSize());
  });

  ngOnInit(): void {
    this.itemState.loadItems();
    this.loadAllTransactions();
  }

  loadAllTransactions(): void {
    this.txnState.loadAllTransactions();
  }

  onPage(e: PageEvent): void {
    this.pageIndex.set(e.pageIndex);
    this.pageSize.set(e.pageSize);
  }

  getItemName(itemId: string): string {
    const item = this.itemState.items().find(i => i.id === itemId);
    return item?.name ?? itemId.substring(0, 8) + '...';
  }

  exportCsv(): void {
    const txns = this.filteredTxns();
    const header = 'Date,Type,Item ID,Delta,Before,After,Note,Created By\n';
    const rows = txns.map(t =>
      `"${t.occurredAt}","${t.type}","${t.itemId}",${t.quantityDelta},${t.stockBefore},${t.stockAfter},"${t.note ?? ''}","${t.createdBy}"`
    ).join('\n');
    const blob = new Blob([header + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'transactions.csv'; a.click();
    URL.revokeObjectURL(url);
  }
}
