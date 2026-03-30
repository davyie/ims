import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MarketStateService } from '../../services/market-state.service';
import { MarketStockStateService } from '../../../market-stock/services/market-stock-state.service';
import { ItemStateService } from '../../../items/services/item-state.service';
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
    CommonModule, RouterModule, ReactiveFormsModule,
    MatTabsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatDividerModule, MatTooltipModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    StatusBadgeComponent, DateFormatPipe, CurrencyFormatPipe,
    StockLevelComponent, PageHeaderComponent
  ],
  templateUrl: './market-detail.component.html',
  styleUrls: ['./market-detail.component.scss']
})
export class MarketDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  state = inject(MarketStateService);
  stockState = inject(MarketStockStateService);
  private itemState = inject(ItemStateService);
  private dialog = inject(MatDialog);
  private txnApi = inject(TransactionApiService);

  transactions = signal<Transaction[]>([]);
  savingItemId = signal<string | null>(null);
  activeSaleItemId = signal<string | null>(null);

  saleForm = this.fb.group({
    price: [0 as number, [Validators.required, Validators.min(0)]],
    currency: ['SEK', Validators.required]
  });

  readonly currencies = ['SEK', 'EUR', 'USD', 'GBP', 'CHF'];
  itemColumns = ['item', 'allocated', 'stock-control', 'price', 'actions'];
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
    this.itemState.loadItems();
  }

  getItemName(itemId: string): string {
    return this.itemState.items().find(i => i.id === itemId)?.name ?? itemId.slice(0, 8) + '…';
  }

  async quickIncrement(mi: MarketItem): Promise<void> {
    this.savingItemId.set(mi.itemId);
    try {
      await this.stockState.increment(this.id, mi.itemId, { quantity: 1 });
    } finally {
      this.savingItemId.set(null);
    }
  }

  openSaleForm(mi: MarketItem): void {
    if (mi.currentStock <= 0) return;
    this.activeSaleItemId.set(mi.itemId);
    this.saleForm.patchValue({ price: mi.marketPrice ?? 0, currency: mi.currency ?? 'SEK' });
  }

  cancelSale(): void {
    this.activeSaleItemId.set(null);
  }

  async confirmSale(mi: MarketItem): Promise<void> {
    if (this.saleForm.invalid) return;
    const v = this.saleForm.getRawValue();
    this.savingItemId.set(mi.itemId);
    try {
      await this.stockState.decrement(this.id, mi.itemId, {
        quantity: 1,
        salePrice: v.price ?? undefined,
        saleCurrency: v.currency ?? undefined
      });
      this.activeSaleItemId.set(null);
    } finally {
      this.savingItemId.set(null);
    }
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
