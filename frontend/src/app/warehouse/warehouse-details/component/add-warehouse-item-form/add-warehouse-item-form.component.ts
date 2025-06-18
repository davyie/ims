import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { WarehouseDetailsService } from '../../warehouse-details.service';

@Component({
  selector: 'app-add-warehouse-item-form',
  standalone: true,
  imports: [FormsModule],
  providers: [WarehouseDetailsService],
  templateUrl: './add-warehouse-item-form.component.html',
  styleUrl: './add-warehouse-item-form.component.css'
})
export class AddWarehouseItemFormComponent {

  @Input()
  warehouseId!: string;

  itemQuantity = {
    item: {
      name: '',
      description: '',
    },
    quantity: 0,
  }

  constructor(private warehouseDetailsService: WarehouseDetailsService) { }

  onSubmit() {
    console.log(this.itemQuantity, this.warehouseId);
    this.warehouseDetailsService.addWarehouseItem(this.itemQuantity, this.warehouseId).subscribe(
      response => {
        console.log("Item added successfully:", response);
        // Optionally, you can reset the form or show a success message here
      },
      error => {
        console.error("Error adding item:", error);
        // Optionally, you can show an error message here
      }
    );
    window.location.reload(); // Reload the page to reflect changes    
    // Logic to handle form submission will go here
  }
}
