import { Component } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CatalogueServiceService } from '../service/catalogue-service.service';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-catalogue-form',
  standalone: true,
  imports: [ReactiveFormsModule, HeaderComponent],
  templateUrl: './catalogue-form.component.html',
  styleUrl: './catalogue-form.component.css'
})

export class CatalogueFormComponent {
  nameControl = new FormControl('');
  descriptionControl = new FormControl('');

  constructor(private catalogueService: CatalogueServiceService) {}

  submitForm() {
    const name = this.nameControl.value;
    const description = this.descriptionControl.value;

    if (name && description) {
      console.log('Form submitted:', { name, description });
      this.catalogueService.postItemByRequest({ name, description, quantity: 1 }).subscribe({
        next: (response) => {
          console.log('Item added successfully:', response);
          // Optionally reset the form controls after successful submission
          this.nameControl.reset();
          this.descriptionControl.reset();
        },
        error: (error) => {
          console.error('Error adding item:', error);
        }
      });
      // Here you can add logic to send the form data to your backend
    } else {
      console.error('Form is invalid');
    }
  }
}
