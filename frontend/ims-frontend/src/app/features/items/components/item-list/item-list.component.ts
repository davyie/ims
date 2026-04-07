import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { ItemStateService } from '../../services/item-state.service';
import { CategoryStateService } from '../../../categories/services/category-state.service';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Item } from '../../../../shared/models/models';

@Component({
  selector: 'app-item-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule,
    MatTableModule, MatInputModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatPaginatorModule, MatCardModule, MatTooltipModule,
    CurrencyFormatPipe, PageHeaderComponent, EmptyStateComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.scss']
})
export class ItemListComponent implements OnInit {
  router = inject(Router);
  state = inject(ItemStateService);
  categoryState = inject(CategoryStateService);
  private dialog = inject(MatDialog);

  searchTerm = signal('');
  selectedCategory = signal('All');
  pageIndex = signal(0);
  pageSize = signal(10);

  displayedColumns = ['sku', 'name', 'category', 'price', 'actions'];

  filteredItems = computed(() => {
    let items = this.state.items();
    const term = this.searchTerm().toLowerCase();
    const cat = this.selectedCategory();
    if (term) {
      items = items.filter(i =>
        i.name.toLowerCase().includes(term) ||
        i.sku.toLowerCase().includes(term) ||
        (i.category ?? '').toLowerCase().includes(term)
      );
    }
    if (cat !== 'All') {
      items = items.filter(i => i.category === cat);
    }
    return items;
  });

  pagedItems = computed(() => {
    const start = this.pageIndex() * this.pageSize();
    return this.filteredItems().slice(start, start + this.pageSize());
  });

  ngOnInit(): void {
    this.state.loadItems();
    this.categoryState.loadCategories();
  }

  onSearch(term: string): void {
    this.searchTerm.set(term);
    this.pageIndex.set(0);
  }

  selectCategory(cat: string): void {
    this.selectedCategory.set(cat);
    this.pageIndex.set(0);
  }

  onPage(e: PageEvent): void {
    this.pageIndex.set(e.pageIndex);
    this.pageSize.set(e.pageSize);
  }

  async deleteItem(item: Item, event: Event): Promise<void> {
    event.stopPropagation();
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Item',
        message: `Delete "${item.name}" (${item.sku})? This cannot be undone.`,
        confirmLabel: 'Delete',
        confirmColor: 'warn'
      }
    });
    const confirmed = await ref.afterClosed().toPromise();
    if (confirmed) await this.state.deleteItem(item.itemId);
  }

  exportCsv(): void {
    const items = this.filteredItems();
    const header = 'SKU,Name,Category,Unit Price,Unit Of Measure\n';
    const rows = items.map(i =>
      `"${i.sku}","${i.name}","${i.category ?? ''}",${i.unitPrice ?? ''},"${i.unitOfMeasure ?? ''}"`
    ).join('\n');
    const blob = new Blob([header + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'items.csv'; a.click();
    URL.revokeObjectURL(url);
  }
}
