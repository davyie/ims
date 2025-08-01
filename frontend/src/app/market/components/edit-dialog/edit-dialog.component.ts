import { Component, Inject } from '@angular/core';
import { ItemQuantity } from '../../market.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-dialog',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './edit-dialog.component.html',
  styleUrl: './edit-dialog.component.css'
})
export class EditDialogComponent {

  formData: ItemQuantity = {
    item: { itemId: 0, name: '', price: 0 },
    quantity: 0
  };

  constructor(@Inject(MAT_DIALOG_DATA) public data: ItemQuantity) {
    // Initialization logic can go here if needed
  }

  ngOnInit(): void {
    console.log("EditDialogComponent initialized with data:", this.data);
    this.formData = this.data;
  }

  onSubmit(): void {
    console.log("Form submitted with data:", this.formData);
    // Logic to handle form submission, e.g., updating the item in the market
    
  }

}
