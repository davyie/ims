import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component';
import { Market, MarketService } from './market.service';
import { NgFor } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-market',
  standalone: true,
  imports: [HeaderComponent, NgFor, RouterModule, HttpClientModule],
  providers: [MarketService],
  templateUrl: './market.component.html',
  styleUrl: './market.component.css'
})
export class MarketComponent {

  constructor(private marketService: MarketService) {}

  markets: Market[] = [];

  ngOnInit(): void {
    console.log("MarketComponent initialized.");
    this.marketService.getMarkets().subscribe(
      data => {
        console.log("Market data fetched successfully:", data);
        this.markets = data;
        console.log("Markets:", this.markets);
      }
      , error => {
        console.error("Error fetching market data:", error);
      }
    );
  }
}
