import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MarketStateService } from '../../services/market-state.service';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { CreateMarketRequest, UpdateMarketRequest } from '../../../../shared/models/models';
import { format, parseISO } from 'date-fns';

@Component({
  selector: 'app-market-form',
  standalone: true,
  imports: [
    CommonModule, RouterModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatDatepickerModule, MatNativeDateModule,
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

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    place: ['', [Validators.required]],
    openDate: [null as Date | null, Validators.required],
    openTime: ['09:00', Validators.required],
    closeDate: [null as Date | null, Validators.required],
    closeTime: ['18:00', Validators.required],
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
        const openDt = parseISO(m.openDate);
        const closeDt = parseISO(m.closeDate);
        this.form.patchValue({
          name: m.name,
          place: m.place,
          openDate: openDt,
          openTime: format(openDt, 'HH:mm'),
          closeDate: closeDt,
          closeTime: format(closeDt, 'HH:mm'),
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
    const openDateTime = `${format(v.openDate!, 'yyyy-MM-dd')}T${v.openTime}:00`;
    const closeDateTime = `${format(v.closeDate!, 'yyyy-MM-dd')}T${v.closeTime}:00`;
    try {
      if (this.isEdit) {
        const req: UpdateMarketRequest = {
          name: v.name!,
          place: v.place!,
          openDate: openDateTime,
          closeDate: closeDateTime,
        };
        await this.state.updateMarket(this.id!, req);
        this.router.navigate(['/markets', this.id]);
      } else {
        const req: CreateMarketRequest = {
          name: v.name!,
          place: v.place!,
          openDate: openDateTime,
          closeDate: closeDateTime,
        };
        const market = await this.state.createMarket(req);
        this.router.navigate(['/markets', market.id]);
      }
    } finally {
      this.saving = false;
    }
  }
}
