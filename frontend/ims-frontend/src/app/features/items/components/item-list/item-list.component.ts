import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ItemStateService } from '../../services/item-state.service';
import { StockLevelComponent } from '../../../../shared/components/stock-level/stock-level.component';
import { CurrencyFormatPipe } from '../../../../shared/pipes/currency-format.pipe';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { Item } from '../../../../shared/models/models';

const CATEGORIES = ['All', 'ELECTRONICS', 'CLOTHING', 'FOOD', 'BEVERAGES', 'ACCESSORIES', 'OTHER'];

@Component({
  selector: 'app-item-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule,
    MatTableModule, MatInputModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatPaginatorModule, MatCardModule, MatTooltipModule,
    StockLevelComponent, CurrencyFormatPipe, PageHeaderComponent, EmptyStateComponent
  ],
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.scss']
})
export class ItemListComponent implements OnInit {
  state = inject(ItemStateService);

  searchTerm = signal('');
  selectedCategory = signal('All');
  pageIndex = signal(0);
  pageSize = signal(10);

  categories = CATEGORIES;
  displayedColumns = ['sku', 'name', 'category', 'price', 'stock', 'actions'];

  filteredItems = computed(() => {
    let items = this.state.items();
    const term = this.searchTerm().toLowerCase();
    const cat = this.selectedCategory();
    if (term) {
      items = items.filter(i =>
        i.name.toLowerCase().includes(term) ||
        i.sku.toLowerCase().includes(term) ||
        i.category.toLowerCase().includes(term)
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

  exportCsv(): void {
    const items = this.filteredItems();
    const header = 'SKU,Name,Category,Price,Currency,Stock\n';
    const rows = items.map(i =>
      `"${i.sku}","${i.name}","${i.category}",${i.defaultPrice},"${i.currency}",${i.totalStorageStock}`
    ).join('\n');
    const blob = new Blob([header + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'items.csv'; a.click();
    URL.revokeObjectURL(url);
  }
}
