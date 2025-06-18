import { Component, Input } from '@angular/core';
import { HeaderComponent } from '../../header/header.component';
import { Warehouse, WarehouseItem } from '../warehouse.service';
import { WarehouseDetailsService } from './warehouse-details.service';
import { NgFor, NgIf } from '@angular/common';
import { AddWarehouseItemFormComponent } from './component/add-warehouse-item-form/add-warehouse-item-form.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { HttpClientModule } from '@angular/common/http';
import { EditItemDialogComponent } from './component/edit-item-dialog/edit-item-dialog.component';

@Component({
  selector: 'app-warehouse-details',
  standalone: true,
  imports: [NgFor, 
    NgIf, 
    HeaderComponent, 
    AddWarehouseItemFormComponent,
    MatDialogModule, 
    HttpClientModule],
  providers: [WarehouseDetailsService],
  templateUrl: './warehouse-details.component.html',
  styleUrl: './warehouse-details.component.css'
})
export class WarehouseDetailsComponent {
  
  @Input() 
  data!: Warehouse;

  warehouseId!: string;

  constructor(private warehouseDetailsService: WarehouseDetailsService,
    private matDialog: MatDialog
  ) {}

  ngOnInit(): void {
    const pathSegments = window.location.pathname.split('/');
    const endOfPath = pathSegments[pathSegments.length - 1];
    this.warehouseId = endOfPath;

    this.warehouseDetailsService.getWarehouseDetails(this.warehouseId).subscribe(
      (data: Warehouse) => { 
        this.data = data as Warehouse;
        console.log("Warehouse details fetched successfully:", this.data);
      }
      , (error: any) => {
        console.error("Error fetching warehouse details:", error);
      }
    );
  }

  openEditDialog(warehouseItem: WarehouseItem) {
    this.matDialog.open(EditItemDialogComponent, {
      data: {
        warehouseId: this.warehouseId,
        warehouseItem: warehouseItem
      }
    });
  }

  deleteWarehouseItem(warehouseId: string, itemId: number) {
    console.log("Deleting item with ID:", itemId, "from warehouse with ID:", warehouseId);
    
    this.warehouseDetailsService.deleteWarehouseItem(warehouseId, itemId).subscribe(
      response => {
        console.log("Item deleted successfully:", response);
        // Optionally, you can refresh the warehouse details or show a success message here
      }
      , error => {  
        console.error("Error deleting item:", error);
        // Optionally, you can show an error message here
      }
    );
    window.location.reload(); // Reload the page to reflect changes
  }
}
