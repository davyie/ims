import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MarketStateService } from '../../services/market-state.service';
import { MarketStockStateService } from '../../../market-stock/services/market-stock-state.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { StockLevelComponent } from '../../../../shared/components/stock-level/stock-level.component';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { TransactionApiService } from '../../../transactions/services/transaction-api.service';
import { Transaction, MarketItem } from '../../../../shared/models/models';
import { StatusBadgeComponent as SBadge } from '../../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-market-detail',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatTabsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatDividerModule,
    StatusBadgeComponent, DateFormatPipe, CurrencyFormatPipe,
    StockLevelComponent, PageHeaderComponent
  ],
  templateUrl: './market-detail.component.html',
  styleUrls: ['./market-detail.component.scss']
})
export class MarketDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  state = inject(MarketStateService);
  stockState = inject(MarketStockStateService);
  private dialog = inject(MatDialog);
  private txnApi = inject(TransactionApiService);

  transactions = signal<Transaction[]>([]);

  itemColumns = ['item', 'allocated', 'current', 'price', 'actions'];
  txnColumns = ['occurred', 'type', 'delta', 'before', 'after', 'note'];

  get id(): string { return this.route.snapshot.paramMap.get('id')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Markets', link: '/markets' },
      { label: this.state.selectedMarket()?.name ?? 'Market' }
    ];
  }

  ngOnInit(): void {
    this.state.loadMarket(this.id);
    this.stockState.loadMarketItems(this.id);
  }

  async openMarket(): Promise<void> {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: { title: 'Open Market', message: 'Open this market?', confirmLabel: 'Open' }
    });
    if (await ref.afterClosed().toPromise()) {
      await this.state.openMarket(this.id);
    }
  }

  async closeMarket(): Promise<void> {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: { title: 'Close Market', message: 'Close this market?', confirmLabel: 'Close', confirmColor: 'warn' }
    });
    if (await ref.afterClosed().toPromise()) {
      await this.state.closeMarket(this.id);
    }
  }
}
