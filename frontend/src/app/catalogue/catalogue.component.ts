import { CommonModule, NgFor } from '@angular/common';
import { Component } from '@angular/core';
import { Item } from '../service/catalogue-service.service';
import { CatalogueServiceService } from '../service/catalogue-service.service';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from '../header/header.component';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [CommonModule, NgFor, HttpClientModule, HeaderComponent, ReactiveFormsModule],
  templateUrl: './catalogue.component.html',
  styleUrl: './catalogue.component.css'
})

export class CatalogueComponent {
  
constructor(private catalogueService: CatalogueServiceService) { }

  items: Item[] = [];

  searchString = new FormControl('');

  onSubmit() {
    console.log("Search string submitted:", this.searchString.value);
  }

  ngOnInit() {
    this.catalogueService.getItemsByRequest().subscribe({
      next: (data) => {
        this.items = data;
        console.log("Items fetched successfully", data);
      },
      error: (err) => {
        console.error("Error fetching items", err);
      }
    });
  }
}
