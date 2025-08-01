import { Component } from '@angular/core';
import { HeaderComponent } from '../../header/header.component';
import { ItemQuantity, MarketService } from '../market.service';
import { NgFor } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditItemDialogComponent } from '../../warehouse/warehouse-details/component/edit-item-dialog/edit-item-dialog.component';
import { EditDialogComponent } from '../components/edit-dialog/edit-dialog.component';
import { AddItemComponent } from '../components/add-item/add-item.component';

@Component({
  selector: 'app-marketdetails',
  standalone: true,
  imports: [HeaderComponent, NgFor, MatDialogModule],
  providers: [MarketService],
  templateUrl: './marketdetails.component.html',
  styleUrl: './marketdetails.component.css'
})
export class MarketdetailsComponent {

  marketName: string = '';
  inventory!: ItemQuantity[];

  constructor(private marketService: MarketService, private matDialog: MatDialog) { 
    // Initialization logic can go here if needed
  }

  ngOnInit(): void {
    const pathSegments = window.location.pathname.split('/');
    this.marketName = pathSegments[pathSegments.length - 1];
    console.log(this.marketName);

    this.marketService.getItems(this.marketName).subscribe(
      (data: ItemQuantity[]) => {
        this.inventory = data;
        console.log("Inventory fetched successfully:", this.inventory);
      }
      , (error) => {
        console.error("Error fetching inventory:", error);
      }
    );
  }

  public incrementQuantity(item: ItemQuantity): void {
    item.quantity++;
    this.updateItem();
  }
  public decrementQuantity(item: ItemQuantity): void {
    if (item.quantity > 0) {
      item.quantity--;
      this.updateItem();
    }
  }

  public updateItem() {
    console.log("Updating item in market:", this.marketName);
  }

  editItem(item: ItemQuantity): void {
    // Logic to open a dialog for editing the item
    this.matDialog.open(EditDialogComponent, {
      data: item
    });
  }

  addItem(): void {
    this.matDialog.open(AddItemComponent, {
      data: {
        marketName: this.marketName
      }
    });
    console.log("Adding new item to market:", this.marketName);
  }
}
