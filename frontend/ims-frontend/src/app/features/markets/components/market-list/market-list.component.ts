import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MarketStateService } from '../../services/market-state.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Market, MarketStatus } from '../../../../shared/models/models';

type TabStatus = 'ALL' | MarketStatus;

@Component({
  selector: 'app-market-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatTabsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatTooltipModule,
    StatusBadgeComponent, DateFormatPipe, PageHeaderComponent, EmptyStateComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './market-list.component.html',
  styleUrls: ['./market-list.component.scss']
})
export class MarketListComponent implements OnInit {
  state = inject(MarketStateService);
  private dialog = inject(MatDialog);
  private router = inject(Router);

  selectedTab = signal<TabStatus>('ALL');
  tabs: TabStatus[] = ['ALL', 'SCHEDULED', 'OPEN', 'CLOSED'];

  filteredMarkets = computed(() => {
    const tab = this.selectedTab();
    if (tab === 'ALL') return this.state.markets();
    return this.state.markets().filter(m => m.status === tab);
  });

  ngOnInit(): void {
    this.state.loadMarkets();
  }

  countByStatus(status: string): number {
    return this.state.markets().filter(m => m.status === status).length;
  }

  selectTab(tab: TabStatus): void {
    this.selectedTab.set(tab);
  }

  async openMarket(market: Market, event: Event): Promise<void> {
    event.stopPropagation();
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Open Market',
        message: `Open "${market.name}"? This will make it active.`,
        confirmLabel: 'Open', confirmColor: 'primary'
      }
    });
    const confirmed = await ref.afterClosed().toPromise();
    if (confirmed) await this.state.openMarket(market.marketId);
  }

  editMarket(market: Market, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/markets', market.marketId, 'edit']);
  }

  async deleteMarket(market: Market, event: Event): Promise<void> {
    event.stopPropagation();
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Market',
        message: `Delete "${market.name}"? This cannot be undone.`,
        confirmLabel: 'Delete',
        confirmColor: 'warn'
      }
    });
    const confirmed = await ref.afterClosed().toPromise();
    if (confirmed) await this.state.deleteMarket(market.marketId);
  }

  async closeMarket(market: Market, event: Event): Promise<void> {
    event.stopPropagation();
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Close Market',
        message: `Close "${market.name}"?`,
        confirmLabel: 'Close', confirmColor: 'warn'
      }
    });
    const confirmed = await ref.afterClosed().toPromise();
    if (confirmed) await this.state.closeMarket(market.marketId);
  }
}
