import { Component, Inject, Input } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WarehouseItem } from '../../../warehouse.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { WarehouseDetailsService } from '../../warehouse-details.service';

@Component({
  selector: 'app-edit-item-dialog',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule],
  providers: [],
  templateUrl: './edit-item-dialog.component.html',
  styleUrl: './edit-item-dialog.component.css'
})
export class EditItemDialogComponent {

  editFormData = {
    item: {
      name: this.data.warehouseItem.item.name || '',
      description: this.data.warehouseItem.item.description || '',
      itemId: this.data.warehouseItem.item.itemId || 0,
    },
    quantity: this.data.warehouseItem.quantity || 0,
  }

  constructor(@Inject(MAT_DIALOG_DATA) public data: { warehouseId: string, warehouseItem: WarehouseItem }, 
  private warehouseDetailsService: WarehouseDetailsService) {}

  onNgInit() {
    console.log(this.data.warehouseId);
  }
  
  onSubmit() {
    // Logic to handle form submission will go here
    this.warehouseDetailsService.updateWarehouseItem(this.data.warehouseId, this.editFormData).subscribe(
      response => {
        console.log("Item updated successfully:", response);
        // Optionally, you can reset the form or show a success message here
      }
      , error => {
        console.error("Error updating item:", error);
        // Optionally, you can show an error message here
      }
    );
    window.location.reload(); // Reload the page to reflect changes
  }
}
