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
import { MarketStateService } from '../../services/market-state.service';
import { MarketStockStateService } from '../../../market-stock/services/market-stock-state.service';
import { ItemStateService } from '../../../items/services/item-state.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { StockLevelComponent } from '../../../../shared/components/stock-level/stock-level.component';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { MarketStock } from '../../../../shared/models/models';

@Component({
  selector: 'app-market-detail',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatTabsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatDividerModule, MatTooltipModule,
    MatFormFieldModule, MatInputModule,
    StatusBadgeComponent, DateFormatPipe,
    StockLevelComponent, PageHeaderComponent, ConfirmDialogComponent
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

  savingItemId = signal<string | null>(null);
  activeSaleItemId = signal<string | null>(null);

  saleForm = this.fb.group({
    quantity: [1, [Validators.required, Validators.min(1)]]
  });

  itemColumns = ['item', 'stock-control', 'actions'];

  get id(): string { return this.route.snapshot.paramMap.get('id')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Markets', link: '/markets' },
      { label: this.state.selectedMarket()?.name ?? 'Market' }
    ];
  }

  ngOnInit(): void {
    this.state.loadMarket(this.id);
    this.stockState.loadMarketStock(this.id);
    this.itemState.loadItems();
  }

  getItemName(itemId: string): string {
    return this.itemState.items().find(i => i.itemId === itemId)?.name ?? itemId.slice(0, 8) + '…';
  }

  async quickIncrement(ms: MarketStock): Promise<void> {
    this.savingItemId.set(ms.itemId);
    try {
      await this.stockState.increment(this.id, { itemId: ms.itemId, quantity: 1 });
    } finally {
      this.savingItemId.set(null);
    }
  }

  openSaleForm(ms: MarketStock): void {
    if (ms.quantity <= 0) return;
    this.activeSaleItemId.set(ms.itemId);
    this.saleForm.patchValue({ quantity: 1 });
  }

  cancelSale(): void {
    this.activeSaleItemId.set(null);
  }

  async confirmSale(ms: MarketStock): Promise<void> {
    if (this.saleForm.invalid) return;
    const qty = this.saleForm.get('quantity')?.value ?? 1;
    this.savingItemId.set(ms.itemId);
    try {
      await this.stockState.decrement(this.id, { itemId: ms.itemId, quantity: qty });
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
