import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { DashboardStateService } from '../../services/dashboard-state.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatCardModule, MatIconModule, MatButtonModule, MatChipsModule,
    StatusBadgeComponent, DateFormatPipe, PageHeaderComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  state = inject(DashboardStateService);

  ngOnInit(): void {
    this.state.loadDashboard();
  }
}
