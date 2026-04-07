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
import { MatDividerModule } from '@angular/material/divider';
import { MarketStockStateService } from '../../services/market-stock-state.service';
import { MarketStockApiService } from '../../services/market-stock-api.service';
import { StockLevelComponent } from '../../../../shared/components/stock-level/stock-level.component';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { MarketStock } from '../../../../shared/models/models';

@Component({
  selector: 'app-market-item-detail',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule, MatTableModule,
    MatFormFieldModule, MatInputModule,
    MatDividerModule,
    StockLevelComponent, DateFormatPipe,
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

  marketStock = signal<MarketStock | null>(null);
  showIncrementForm = signal(false);
  showDecrementForm = signal(false);
  saving = signal(false);

  incrementForm = this.fb.group({
    quantity: [1, [Validators.required, Validators.min(1)]],
  });

  decrementForm = this.fb.group({
    quantity: [1, [Validators.required, Validators.min(1)]],
  });

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
  }

  loadItem(): void {
    // Load all stock for this market and find this item's stock entry
    this.api.getMarketStock(this.marketId).subscribe({
      next: page => {
        const entry = page.content.find(s => s.itemId === this.itemId) ?? null;
        this.marketStock.set(entry);
      }
    });
  }

  async applyIncrement(): Promise<void> {
    if (this.incrementForm.invalid) return;
    this.saving.set(true);
    const qty = this.incrementForm.get('quantity')?.value ?? 1;
    try {
      await this.state.increment(this.marketId, { itemId: this.itemId, quantity: qty });
      this.showIncrementForm.set(false);
      this.loadItem();
    } finally { this.saving.set(false); }
  }

  async applyDecrement(): Promise<void> {
    if (this.decrementForm.invalid) return;
    this.saving.set(true);
    const qty = this.decrementForm.get('quantity')?.value ?? 1;
    try {
      await this.state.decrement(this.marketId, { itemId: this.itemId, quantity: qty });
      this.showDecrementForm.set(false);
      this.loadItem();
    } finally { this.saving.set(false); }
  }
}
