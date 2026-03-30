import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDividerModule } from '@angular/material/divider';
import { StorageStateService } from '../../services/storage-state.service';
import { StockLevelComponent } from '../../../../shared/components/stock-level/stock-level.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { StorageItem } from '../../../../shared/models/models';

@Component({
  selector: 'app-storage-overview',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule,
    MatCardModule, MatExpansionModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatDividerModule,
    StockLevelComponent, PageHeaderComponent
  ],
  templateUrl: './storage-overview.component.html',
  styleUrls: ['./storage-overview.component.scss']
})
export class StorageOverviewComponent implements OnInit {
  state = inject(StorageStateService);

  searchTerm = signal('');

  filteredItems = computed(() => {
    const term = this.searchTerm().toLowerCase();
    if (!term) return this.state.storageItems();
    return this.state.storageItems().filter(i =>
      i.name.toLowerCase().includes(term) ||
      i.sku.toLowerCase().includes(term) ||
      i.category.toLowerCase().includes(term)
    );
  });

  zones = computed(() => {
    const items = this.filteredItems();
    const zoneMap = new Map<string, StorageItem[]>();
    items.forEach(item => {
      const zone = item.category; // group by category as zone proxy
      if (!zoneMap.has(zone)) zoneMap.set(zone, []);
      zoneMap.get(zone)!.push(item);
    });
    return Array.from(zoneMap.entries()).map(([zone, items]) => ({ zone, items }));
  });

  ngOnInit(): void {
    this.state.loadStorageItems();
  }
}
