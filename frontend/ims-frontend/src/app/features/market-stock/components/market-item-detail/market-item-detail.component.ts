import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatTabsModule } from '@angular/material/tabs';
import { MarketStockStateService } from '../../services/market-stock-state.service';
import { MarketStockApiService } from '../../services/market-stock-api.service';
import { StockLevelComponent } from '../../../../shared/components/stock-level/stock-level.component';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { Transaction, MarketItem } from '../../../../shared/models/models';

@Component({
  selector: 'app-market-item-detail',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule, MatTableModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatDialogModule,
    MatDividerModule, MatTabsModule,
    StockLevelComponent, StatusBadgeComponent, CurrencyFormatPipe, DateFormatPipe,
    PageHeaderComponent
  ],
  templateUrl: './market-item-detail.component.html',
  styleUrls: ['./market-item-detail.component.scss']
})
export class MarketItemDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  state = inject(MarketStockStateService);
  private api = inject(MarketStockApiService);
  private fb = inject(FormBuilder);

  transactions = signal<Transaction[]>([]);
  marketItem = signal<MarketItem | null>(null);
  showIncrementForm = signal(false);
  showDecrementForm = signal(false);
  showPriceForm = signal(false);
  saving = signal(false);

  currencies = ['SEK', 'EUR', 'USD', 'GBP', 'CHF'];

  incrementForm = this.fb.group({
    quantity: [1, [Validators.required, Validators.min(1)]],
    note: [''],
    createdBy: ['system']
  });

  decrementForm = this.fb.group({
    quantity: [1, [Validators.required, Validators.min(1)]],
    note: [''],
    createdBy: ['system'],
    salePrice: [null as number | null, [Validators.min(0)]],
    saleCurrency: ['SEK']
  });

  priceForm = this.fb.group({
    price: [0, [Validators.required, Validators.min(0)]],
    currency: ['SEK', Validators.required]
  });

  txnColumns = ['occurred', 'type', 'delta', 'before', 'after', 'sale-price', 'note'];

  get marketId(): string { return this.route.snapshot.paramMap.get('id')!; }
  get itemId(): string { return this.route.snapshot.paramMap.get('itemId')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Markets', link: '/markets' },
      { label: 'Market', link: `/markets/${this.marketId}` },
      { label: 'Item Detail' }
    ];
  }

  ngOnInit(): void {
    this.loadItem();
    this.loadTransactions();
  }

  loadItem(): void {
    this.api.getMarketItem(this.marketId, this.itemId).subscribe({
      next: item => {
        this.marketItem.set(item);
        this.priceForm.patchValue({ price: item.marketPrice, currency: item.currency });
        this.decrementForm.patchValue({ salePrice: item.marketPrice, saleCurrency: item.currency });
      }
    });
  }

  loadTransactions(): void {
    this.api.getMarketItemTransactions(this.marketId, this.itemId).subscribe({
      next: txns => this.transactions.set(txns)
    });
  }

  async applyIncrement(): Promise<void> {
    if (this.incrementForm.invalid) return;
    this.saving.set(true);
    const v = this.incrementForm.getRawValue();
    try {
      await this.state.increment(this.marketId, this.itemId, {
        quantity: v.quantity!, note: v.note || undefined, createdBy: v.createdBy || undefined
      });
      this.showIncrementForm.set(false);
      this.loadItem();
      this.loadTransactions();
    } finally { this.saving.set(false); }
  }

  async applyDecrement(): Promise<void> {
    if (this.decrementForm.invalid) return;
    this.saving.set(true);
    const v = this.decrementForm.getRawValue();
    try {
      await this.state.decrement(this.marketId, this.itemId, {
        quantity: v.quantity!,
        note: v.note || undefined,
        createdBy: v.createdBy || undefined,
        salePrice: v.salePrice ?? undefined,
        saleCurrency: v.saleCurrency || undefined
      });
      this.showDecrementForm.set(false);
      this.loadItem();
      this.loadTransactions();
    } finally { this.saving.set(false); }
  }

  async updatePrice(): Promise<void> {
    if (this.priceForm.invalid) return;
    this.saving.set(true);
    const v = this.priceForm.getRawValue();
    try {
      await this.state.setPrice(this.marketId, this.itemId, {
        price: v.price!, currency: v.currency!
      });
      this.showPriceForm.set(false);
      this.loadItem();
    } finally { this.saving.set(false); }
  }
}
