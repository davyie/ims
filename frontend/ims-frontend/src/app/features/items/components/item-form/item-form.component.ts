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
import { CategoryStateService } from '../../../categories/services/category-state.service';
import { WarehouseStateService } from '../../../storage/services/warehouse-state.service';
import { WarehouseApiService } from '../../../storage/services/warehouse-api.service';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { CreateItemRequest, UpdateItemRequest } from '../../../../shared/models/models';
import { NotificationService } from '../../../../core/services/notification.service';

const UNIT_OF_MEASURES = ['pcs', 'kg', 'g', 'L', 'mL', 'box', 'pack', 'pair', 'set', 'unit'];

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
  private notify = inject(NotificationService);
  categoryState = inject(CategoryStateService);
  private warehouseState = inject(WarehouseStateService);
  private warehouseApi = inject(WarehouseApiService);

  unitOfMeasures = UNIT_OF_MEASURES;
  isEdit = false;
  saving = false;

  form: FormGroup = this.fb.group({
    sku: ['', [Validators.required, Validators.pattern(/^[A-Z0-9\-]+$/)]],
    name: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    category: [''],
    unitOfMeasure: ['pcs'],
    unitPrice: [null as number | null, [Validators.min(0)]],
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
    this.categoryState.loadCategories();
    this.warehouseState.loadWarehouses();
    if (this.id) {
      this.isEdit = true;
      this.state.loadItem(this.id);
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
          category: item.category ?? '',
          unitOfMeasure: item.unitOfMeasure ?? 'pcs',
          unitPrice: item.unitPrice ?? null,
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
          name: v.name,
          description: v.description || undefined,
          category: v.category || undefined,
          unitOfMeasure: v.unitOfMeasure || undefined,
          unitPrice: v.unitPrice ?? undefined,
        };
        await this.state.updateItem(this.id!, req);
        this.router.navigate(['/items', this.id]);
      } else {
        const req: CreateItemRequest = {
          sku: v.sku,
          name: v.name,
          description: v.description || undefined,
          category: v.category || undefined,
          unitOfMeasure: v.unitOfMeasure || undefined,
          unitPrice: v.unitPrice ?? undefined,
        };
        const item = await this.state.createItem(req);

        // If initial stock > 0, add to default warehouse
        const initialStock = v.initialStock ?? 0;
        if (initialStock > 0) {
          const warehouseId = this.warehouseState.defaultWarehouseId();
          if (warehouseId) {
            await this.warehouseApi.addStock(warehouseId, {
              itemId: item.itemId,
              quantity: initialStock
            }).toPromise();
          } else {
            this.notify.error('No warehouse', 'Create a warehouse first to add stock');
          }
        }

        this.router.navigate(['/items', item.itemId]);
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
