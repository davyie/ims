import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDividerModule } from '@angular/material/divider';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ItemStateService } from '../../services/item-state.service';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { PageHeaderComponent, Breadcrumb } from '../../../../shared/components/page-header/page-header.component';
import { StockAdjustComponent } from '../stock-adjust/stock-adjust.component';

@Component({
  selector: 'app-item-detail',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatTabsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatTableModule, MatDividerModule, MatSidenavModule,
    DateFormatPipe, PageHeaderComponent, StockAdjustComponent
  ],
  templateUrl: './item-detail.component.html',
  styleUrls: ['./item-detail.component.scss']
})
export class ItemDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  state = inject(ItemStateService);

  adjustPanelOpen = signal(false);

  get id(): string { return this.route.snapshot.paramMap.get('id')!; }

  get breadcrumbs(): Breadcrumb[] {
    return [
      { label: 'Items', link: '/items' },
      { label: this.state.selectedItem()?.name ?? 'Item' }
    ];
  }

  ngOnInit(): void {
    this.state.loadItem(this.id);
  }

  openAdjustPanel(): void {
    this.adjustPanelOpen.set(true);
  }

  onAdjustClose(): void {
    this.adjustPanelOpen.set(false);
  }
}
