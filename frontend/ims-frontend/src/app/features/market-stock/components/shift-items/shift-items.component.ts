import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormArray, FormGroup, Validators } from '@angular/forms';
import { MatStepperModule } from '@angular/material/stepper';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { ItemStateService } from '../../../items/services/item-state.service';
import { MarketStockStateService } from '../../services/market-stock-state.service';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { Item, ShiftItemRequest } from '../../../../shared/models/models';

const CURRENCIES = ['EUR', 'USD', 'GBP', 'CHF', 'SEK'];

@Component({
  selector: 'app-shift-items',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatStepperModule, MatCardModule, MatCheckboxModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatTableModule,
    PageHeaderComponent, CurrencyFormatPipe
  ],
  templateUrl: './shift-items.component.html',
  styleUrls: ['./shift-items.component.scss']
})
export class ShiftItemsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private itemState = inject(ItemStateService);
  stockState = inject(MarketStockStateService);

  currencies = CURRENCIES;
  selectedItems = signal<Item[]>([]);
  saving = signal(false);

  quantityForm: FormGroup = this.fb.group({ items: this.fb.array([]) });

  get marketId(): string { return this.route.snapshot.paramMap.get('id')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Markets', link: '/markets' },
      { label: 'Market', link: `/markets/${this.marketId}` },
      { label: 'Shift Items' }
    ];
  }

  get availableItems(): Item[] {
    return this.itemState.items().filter(i => i.totalStorageStock > 0);
  }

  get itemsArray(): FormArray { return this.quantityForm.get('items') as FormArray; }

  ngOnInit(): void {
    this.itemState.loadItems();
  }

  isSelected(item: Item): boolean {
    return this.selectedItems().some(i => i.id === item.id);
  }

  toggleItem(item: Item): void {
    const current = this.selectedItems();
    if (this.isSelected(item)) {
      this.selectedItems.set(current.filter(i => i.id !== item.id));
    } else {
      this.selectedItems.set([...current, item]);
    }
  }

  buildQuantityForms(): void {
    const fa = this.itemsArray;
    while (fa.length) fa.removeAt(0);
    this.selectedItems().forEach(item => {
      fa.push(this.fb.group({
        itemId: [item.id],
        quantity: [1, [Validators.required, Validators.min(1), Validators.max(item.totalStorageStock)]],
        marketPrice: [item.defaultPrice, [Validators.required, Validators.min(0)]],
        currency: [item.currency, Validators.required],
      }));
    });
  }

  async submit(): Promise<void> {
    if (this.quantityForm.invalid) return;
    this.saving.set(true);
    const items = this.itemsArray.getRawValue();
    try {
      for (const item of items) {
        const req: ShiftItemRequest = {
          itemId: item.itemId,
          quantity: item.quantity,
          marketPrice: item.marketPrice,
          currency: item.currency,
        };
        await this.stockState.shiftItem(this.marketId, req);
      }
      this.router.navigate(['/markets', this.marketId]);
    } finally {
      this.saving.set(false);
    }
  }
}
