import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDividerModule } from '@angular/material/divider';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ItemStateService } from '../../services/item-state.service';
import { ItemApiService } from '../../services/item-api.service';
import { StockLevelComponent } from '../../../../shared/components/stock-level/stock-level.component';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { TransactionTypePipe } from '../../../../shared/pipes/transaction-type.pipe';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { StockAdjustComponent } from '../stock-adjust/stock-adjust.component';
import { Transaction } from '../../../../shared/models/models';

@Component({
  selector: 'app-item-detail',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatTabsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatDividerModule, MatSidenavModule,
    StockLevelComponent, StatusBadgeComponent, CurrencyFormatPipe,
    DateFormatPipe, TransactionTypePipe, PageHeaderComponent, StockAdjustComponent
  ],
  templateUrl: './item-detail.component.html',
  styleUrls: ['./item-detail.component.scss']
})
export class ItemDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  state = inject(ItemStateService);
  private api = inject(ItemApiService);

  adjustPanelOpen = signal(false);
  transactions = signal<Transaction[]>([]);

  txnColumns = ['occurred', 'type', 'delta', 'before', 'after', 'note', 'createdBy'];

  get id(): string { return this.route.snapshot.paramMap.get('id')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Items', link: '/items' },
      { label: this.state.selectedItem()?.name ?? 'Item' }
    ];
  }

  ngOnInit(): void {
    this.state.loadItem(this.id);
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.api.getItemTransactions(this.id).subscribe({
      next: txns => this.transactions.set(txns),
      error: () => {}
    });
  }

  openAdjustPanel(): void {
    this.adjustPanelOpen.set(true);
  }

  onAdjustClose(): void {
    this.adjustPanelOpen.set(false);
    this.state.loadItem(this.id);
    this.loadTransactions();
  }
}
