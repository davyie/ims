import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ItemQuantity {
  item: MarketItem;
  quantity: number;
}

export interface MarketItem {
  itemId?: number;
  name: string;
  price: number;
}

export interface Market {
  name: string;
  price: number; 
  inventory: ItemQuantity[];
}

@Injectable({
  providedIn: 'root'
})

export class MarketService {

  commandBaseUrl: string = "http://localhost:8080/api/market/command";
  queryBaseUrl: string = "http://localhost:8080/api/market/query";

  constructor(private http: HttpClient) { }

  public createMarket(ItemQuantity: ItemQuantity): Observable<any> {
    const apiUrl = this.commandBaseUrl + '/create';
    return this.http.post(apiUrl, ItemQuantity);
  }

  public getMarkets(): Observable<Market[]> {
    console.log("Running getMarkets() in MarketService");
    const apiUrl = this.queryBaseUrl + '/get/all';
    return this.http.get<Market[]>(apiUrl);
  }

  public addItemToMarket(warehouseId: string, marketName: string, itemId: number, quantity: number): Observable<any> {
    const apiUrl = this.commandBaseUrl + '/add/item';
    console.log("Adding item to market:", {
      warehouseId: warehouseId,
      marketName: decodeURIComponent(marketName), 
      itemId: itemId,
      quantity: quantity
    });
    return this.http.post(apiUrl, {
      warehouseId: warehouseId,
      marketName: decodeURIComponent(marketName),
      itemId: itemId,
      quantity: quantity
    });
  }
  
  public removeItemFromMarket(marketName: string, itemId: number): Observable<any> {
    const apiUrl = `${this.commandBaseUrl}/remove/${marketName}/${itemId}`;
    return this.http.delete(apiUrl);
  }

  public getItems(marketName: string): Observable<ItemQuantity[]> {
    const apiUrl = this.queryBaseUrl + "/inventory?marketName=" + marketName;
    return this.http.get<ItemQuantity[]>(apiUrl);
  }
}
