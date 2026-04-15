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
import { WarehouseStateService } from '../../../storage/services/warehouse-state.service';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { Item } from '../../../../shared/models/models';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-shift-items',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatStepperModule, MatCardModule, MatCheckboxModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatTableModule,
    PageHeaderComponent
  ],
  templateUrl: './shift-items.component.html',
  styleUrls: ['./shift-items.component.scss']
})
export class ShiftItemsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private itemState = inject(ItemStateService);
  private stockState = inject(MarketStockStateService);
  warehouseState = inject(WarehouseStateService);
  private notify = inject(NotificationService);

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
    return this.itemState.items();
  }

  get itemsArray(): FormArray { return this.quantityForm.get('items') as FormArray; }

  ngOnInit(): void {
    this.itemState.loadItems();
    this.warehouseState.loadWarehouses();
  }

  isSelected(item: Item): boolean {
    return this.selectedItems().some(i => i.itemId === item.itemId);
  }

  toggleItem(item: Item): void {
    const current = this.selectedItems();
    if (this.isSelected(item)) {
      this.selectedItems.set(current.filter(i => i.itemId !== item.itemId));
    } else {
      this.selectedItems.set([...current, item]);
    }
  }

  buildQuantityForms(): void {
    const fa = this.itemsArray;
    while (fa.length) fa.removeAt(0);
    this.selectedItems().forEach(item => {
      fa.push(this.fb.group({
        itemId: [item.itemId],
        quantity: [1, [Validators.required, Validators.min(1)]],
      }));
    });
  }

  async submit(): Promise<void> {
    if (this.quantityForm.invalid) return;
    const warehouseId = this.warehouseState.defaultWarehouseId();
    if (!warehouseId) {
      this.notify.error('No warehouse', 'Please create a warehouse before shifting items');
      return;
    }

    this.saving.set(true);
    const items = this.itemsArray.getRawValue();
    try {
      for (const row of items) {
        await this.stockState.shiftToMarket({
          itemId: row.itemId,
          quantity: row.quantity,
          sourceType: 'WAREHOUSE',
          sourceId: warehouseId,
          destinationType: 'MARKET',
          destinationId: this.marketId,
        });
      }
      this.router.navigate(['/markets', this.marketId], { state: { shifted: true } });
    } catch (err: unknown) {
      const msg = (err instanceof Error) ? err.message : 'Transfer failed';
      this.notify.error('Shift failed', msg);
    } finally {
      this.saving.set(false);
    }
  }
}
