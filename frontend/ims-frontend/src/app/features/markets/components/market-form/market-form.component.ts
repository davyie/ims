import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
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
import { CreateMarketRequest } from '../../../../shared/models/models';
import { format } from 'date-fns';

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
export class MarketFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private state = inject(MarketStateService);

  saving = false;

  breadcrumbs: Breadcrumb[] = [
    { label: 'Markets', link: '/markets' },
    { label: 'New Market' }
  ];

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    place: ['', [Validators.required]],
    openDate: [null as Date | null, Validators.required],
    closeDate: [null as Date | null, Validators.required],
  });

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    const v = this.form.getRawValue();
    const req: CreateMarketRequest = {
      name: v.name!,
      place: v.place!,
      openDate: format(v.openDate!, 'yyyy-MM-dd'),
      closeDate: format(v.closeDate!, 'yyyy-MM-dd'),
    };
    try {
      const market = await this.state.createMarket(req);
      this.router.navigate(['/markets', market.id]);
    } finally {
      this.saving = false;
    }
  }
}
