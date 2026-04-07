import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MarketStateService } from '../../services/market-state.service';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { CreateMarketRequest, UpdateMarketRequest, MarketType } from '../../../../shared/models/models';

const MARKET_TYPES: { value: MarketType; label: string }[] = [
  { value: 'FARMERS_MARKET', label: 'Farmers Market' },
  { value: 'RETAIL', label: 'Retail' },
  { value: 'WHOLESALE', label: 'Wholesale' },
  { value: 'POP_UP', label: 'Pop-Up' },
  { value: 'OTHER', label: 'Other' },
];

@Component({
  selector: 'app-market-form',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule,
    PageHeaderComponent
  ],
  templateUrl: './market-form.component.html',
  styleUrls: ['./market-form.component.scss']
})
export class MarketFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private state = inject(MarketStateService);

  isEdit = false;
  saving = false;
  marketTypes = MARKET_TYPES;

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    location: [''],
    marketType: ['FARMERS_MARKET' as MarketType, Validators.required],
    description: [''],
  });

  get id(): string | null { return this.route.snapshot.paramMap.get('id'); }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Markets', link: '/markets' },
      { label: this.isEdit ? 'Edit Market' : 'New Market' }
    ];
  }

  get title(): string { return this.isEdit ? 'Edit Market' : 'Create Market'; }

  ngOnInit(): void {
    if (this.id) {
      this.isEdit = true;
      this.state.loadMarket(this.id);
    }
  }

  ngDoCheck(): void {
    if (this.isEdit && this.state.selectedMarket()) {
      const m = this.state.selectedMarket()!;
      if (this.form.get('name')?.value === '') {
        this.form.patchValue({
          name: m.name,
          location: m.location ?? '',
          marketType: m.marketType,
          description: m.description ?? '',
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
        const req: UpdateMarketRequest = {
          name: v.name!,
          location: v.location || undefined,
          description: v.description || undefined,
        };
        await this.state.updateMarket(this.id!, req);
        this.router.navigate(['/markets', this.id]);
      } else {
        const req: CreateMarketRequest = {
          name: v.name!,
          location: v.location || undefined,
          marketType: v.marketType!,
          description: v.description || undefined,
        };
        const market = await this.state.createMarket(req);
        this.router.navigate(['/markets', market.marketId]);
      }
    } finally {
      this.saving = false;
    }
  }
}
