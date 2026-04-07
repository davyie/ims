import { Component, Input, Output, EventEmitter, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { Item } from '../../../../shared/models/models';
import { WarehouseStateService } from '../../../storage/services/warehouse-state.service';
import { WarehouseApiService } from '../../../storage/services/warehouse-api.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-stock-adjust',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatDividerModule
  ],
  templateUrl: './stock-adjust.component.html',
  styleUrls: ['./stock-adjust.component.scss']
})
export class StockAdjustComponent {
  @Input() item!: Item;
  @Output() close = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private warehouseState = inject(WarehouseStateService);
  private warehouseApi = inject(WarehouseApiService);
  private notify = inject(NotificationService);

  saving = signal(false);

  form = this.fb.group({
    delta: [0, [Validators.required]],
    note: [''],
  });

  increment(): void {
    const cur = this.form.get('delta')?.value ?? 0;
    this.form.get('delta')?.setValue(cur + 1);
  }

  decrement(): void {
    const cur = this.form.get('delta')?.value ?? 0;
    this.form.get('delta')?.setValue(cur - 1);
  }

  async submit(): Promise<void> {
    if (this.form.invalid) return;
    const delta = this.form.get('delta')?.value ?? 0;
    if (delta === 0) {
      this.notify.error('Invalid', 'Delta must be non-zero');
      return;
    }

    const warehouseId = this.warehouseState.defaultWarehouseId();
    if (!warehouseId) {
      this.notify.error('No warehouse', 'Please create a warehouse first');
      return;
    }

    this.saving.set(true);
    try {
      if (delta > 0) {
        await this.warehouseApi.addStock(warehouseId, {
          itemId: this.item.itemId,
          quantity: delta
        }).toPromise();
        this.notify.success(`Added ${delta} units to warehouse`);
      } else {
        await this.warehouseApi.removeStock(warehouseId, {
          itemId: this.item.itemId,
          quantity: Math.abs(delta)
        }).toPromise();
        this.notify.success(`Removed ${Math.abs(delta)} units from warehouse`);
      }
      this.close.emit();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Stock adjustment failed';
      this.notify.error('Error', msg);
    } finally {
      this.saving.set(false);
    }
  }
}
