import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface WarehouseItem {
  itemId?: number;
  item: Item;
  quantity: number;
}

export interface Item {
  name: string;
  description: string;
  itemId?: number;
}

export interface Warehouse {
  id: number;
  inventory: WarehouseItem[];
}

@Injectable({
  providedIn: 'root'
})


export class WarehouseService {

  baseUrl: String = "http://localhost:8080/warehouse";

  constructor(private http: HttpClient) { }

  createWarehouse(): Observable<any> {
    const apiUrl = this.baseUrl + '/add'; 
    return this.http.post(apiUrl, {});
  }

  getWarehouseData(): Observable<any> {
    const apiUrl = this.baseUrl + '/get/all';
    return this.http.get(apiUrl);
  }
}
