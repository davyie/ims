import { Component, Input, Output, EventEmitter, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { Item, AdjustStockRequest } from '../../../../shared/models/models';
import { ItemStateService } from '../../services/item-state.service';

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
  private state = inject(ItemStateService);

  saving = signal(false);
  delta = signal(0);

  form = this.fb.group({
    delta: [0, [Validators.required]],
    note: [''],
    createdBy: ['system'],
  });

  get newStock(): number {
    return Math.max(0, this.item.totalStorageStock + (this.form.get('delta')?.value ?? 0));
  }

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
    this.saving.set(true);
    const v = this.form.getRawValue();
    const req: AdjustStockRequest = {
      delta: v.delta!,
      note: v.note || undefined,
      createdBy: v.createdBy || undefined,
    };
    try {
      await this.state.adjustStock(this.item.id, req);
      this.close.emit();
    } finally {
      this.saving.set(false);
    }
  }
}
