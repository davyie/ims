import { Component, Inject } from '@angular/core';
import { Warehouse, WarehouseItem, WarehouseService } from '../../../warehouse/warehouse.service';
import { NgFor } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { FormsModule, NgModel } from '@angular/forms';
import { MarketService } from '../../market.service';

@Component({
  selector: 'app-add-item',
  standalone: true,
  imports: [NgFor, MatDialogModule, FormsModule],
  providers: [WarehouseService],
  templateUrl: './add-item.component.html',
  styleUrl: './add-item.component.css'
})

export class AddItemComponent {
  warehouses: Warehouse[] = [];
  warehouseId: string = '';
  warehouseInventory: WarehouseItem[] = [];
  quantity: number = 0;
  selectedItem: number = 0;
  marketName: string = this.data.marketName;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {marketName: string}, 
    private warehouseService: WarehouseService, 
    private marketService: MarketService) { }

  ngOnInit(): void {
    this.warehouseService.getWarehouseData().subscribe(
      (data: Warehouse[]) => {
        this.warehouses = data;
        console.log("Warehouses fetched successfully:", this.warehouses);
      },
      (error) => {
        console.error("Error fetching warehouses:", error);
      }
    );
  }

  onSubmit() {
    // console.log("Selected Warehouse ID:", this.warehouseId);
    // console.log("Selected Item:", this.selectedItem);
    // console.log("Selected Quantity:", this.quantity);
    // console.log("Selected market:", this.marketName);
    this.marketService.addItemToMarket(this.warehouseId, this.marketName, this.selectedItem, this.quantity).subscribe(
      response => {
        console.log("Item added successfully:", response);
        // Optionally, you can reset the form or show a success message here
      }
      , error => {
        console.error("Error adding item:", error);
        // Optionally, you can show an error message here
      }
    );
    // window.location.reload(); // Reload the page to reflect changes
  }

  onSelectionChange() {
    console.log(this.warehouseId);
    this.warehouseService.getWarehouseData().subscribe(
      (data: Warehouse[]) => {
        const selectedWarehouse = data.find(warehouse => warehouse.id === this.warehouseId);
        if (selectedWarehouse) {
          this.warehouseInventory = selectedWarehouse.inventory;
          console.log("Selected Warehouse Inventory:", this.warehouseInventory);
        } else {
          console.error("Selected warehouse not found");
        }
      }
      , (error) => {  
        console.error("Error fetching warehouse inventory:", error);
      }
    );
  }
}
