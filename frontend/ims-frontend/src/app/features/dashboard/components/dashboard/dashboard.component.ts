import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { DashboardStateService } from '../../services/dashboard-state.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatCardModule, MatIconModule, MatButtonModule, MatTableModule, MatChipsModule,
    StatusBadgeComponent, DateFormatPipe, CurrencyFormatPipe, PageHeaderComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  state = inject(DashboardStateService);

  txnColumns = ['occurred', 'type', 'item', 'delta', 'note'];

  ngOnInit(): void {
    this.state.loadDashboard();
  }

  get totalRevenue(): number {
    return this.state.allSummary()?.totalRevenue ?? 0;
  }

  get currency(): string {
    return this.state.allSummary()?.currency ?? 'EUR';
  }

  get totalItemsSold(): number {
    return this.state.allSummary()?.totalItemsSold ?? 0;
  }
}
