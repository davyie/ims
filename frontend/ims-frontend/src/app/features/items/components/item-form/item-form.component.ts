import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ItemStateService } from '../../services/item-state.service';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { RegisterItemRequest, UpdateItemRequest } from '../../../../shared/models/models';

const CATEGORIES = ['ELECTRONICS', 'CLOTHING', 'FOOD', 'BEVERAGES', 'ACCESSORIES', 'OTHER'];
const CURRENCIES = ['EUR', 'USD', 'GBP', 'CHF', 'SEK'];

@Component({
  selector: 'app-item-form',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, PageHeaderComponent
  ],
  templateUrl: './item-form.component.html',
  styleUrls: ['./item-form.component.scss']
})
export class ItemFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private state = inject(ItemStateService);

  categories = CATEGORIES;
  currencies = CURRENCIES;
  isEdit = false;
  saving = false;

  form: FormGroup = this.fb.group({
    sku: ['', [Validators.required, Validators.pattern(/^[A-Z0-9\-]+$/)]],
    name: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    category: ['', Validators.required],
    defaultPrice: [0, [Validators.required, Validators.min(0)]],
    currency: ['EUR', Validators.required],
    zone: ['', Validators.required],
    shelf: ['', Validators.required],
    row: [1, [Validators.required, Validators.min(1)]],
    column: [1, [Validators.required, Validators.min(1)]],
    initialStock: [0, [Validators.required, Validators.min(0)]],
  });

  get id(): string | null { return this.route.snapshot.paramMap.get('id'); }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Items', link: '/items' },
      { label: this.isEdit ? 'Edit Item' : 'New Item' }
    ];
  }

  get title(): string { return this.isEdit ? 'Edit Item' : 'Register Item'; }

  ngOnInit(): void {
    if (this.id) {
      this.isEdit = true;
      this.state.loadItem(this.id);
      // Remove initialStock for edit mode
      this.form.get('initialStock')?.disable();
      this.form.get('sku')?.disable();
    }
  }

  ngDoCheck(): void {
    if (this.isEdit && this.state.selectedItem()) {
      const item = this.state.selectedItem()!;
      if (this.form.get('name')?.value === '') {
        this.form.patchValue({
          name: item.name,
          description: item.description ?? '',
          category: item.category,
          defaultPrice: item.defaultPrice,
          currency: item.currency,
          zone: item.zone,
          shelf: item.shelf,
          row: item.row,
          column: item.column,
        });
      }
    }
  }

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    const v = this.form.getRawValue();
    try {
      if (this.isEdit) {
        const req: UpdateItemRequest = {
          name: v.name, description: v.description || undefined,
          category: v.category, defaultPrice: v.defaultPrice, currency: v.currency,
          zone: v.zone, shelf: v.shelf, row: v.row, column: v.column
        };
        await this.state.updateItem(this.id!, req);
        this.router.navigate(['/items', this.id]);
      } else {
        const req: RegisterItemRequest = {
          sku: v.sku, name: v.name, description: v.description || undefined,
          category: v.category, defaultPrice: v.defaultPrice, currency: v.currency,
          zone: v.zone, shelf: v.shelf, row: v.row, column: v.column,
          initialStock: v.initialStock
        };
        const item = await this.state.registerItem(req);
        this.router.navigate(['/items', item.id]);
      }
    } finally {
      this.saving = false;
    }
  }

  cancel(): void {
    if (this.isEdit) {
      this.router.navigate(['/items', this.id]);
    } else {
      this.router.navigate(['/items']);
    }
  }
}
