import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component';
import { Warehouse, WarehouseService } from './warehouse.service';
import { NgFor } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-warehouse',
  standalone: true,
  imports: [HeaderComponent, NgFor, RouterModule],
  templateUrl: './warehouse.component.html',
  styleUrl: './warehouse.component.css'
})

export class WarehouseComponent {

  warehouses: Warehouse[] = [];

  constructor(private warehouseService: WarehouseService) {}

  ngOnInit(): void {
    console.log("WarehouseComponent initialized.");
    this.fetchWarehouseData();
  }

  fetchWarehouseData() {
    // Logic to fetch warehouse data will go here
    console.log("Fetching warehouse data...");
    this.warehouseService.getWarehouseData().subscribe(
      data => {
        console.log("Warehouse data fetched successfully:", data);
        this.warehouses = data;
        console.log("Warehouses:", this.warehouses);
      }
      , error => {
        console.error("Error fetching warehouse data:", error);
      }
    );
  }

  createWarehouse() {
    // Logic to create a new warehouse will go here
    console.log("Creating a new warehouse...");
  }

  deleteWarehouse() {
    // Logic to delete a warehouse will go here
    console.log("Deleting a warehouse...");
  }
}
