import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Warehouse, WarehouseItem } from '../warehouse/warehouse.service';
import { Observable } from 'rxjs';

export interface ChangeQuantity {
  itemId: number;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class WarehouseDetailsService {

  data!: Warehouse;
  baseUrl: string = "http://localhost:8080/warehouse";

  constructor(private http: HttpClient) { }

  getWarehouseDetails(warehouseId: string): Observable<Warehouse> {
    const apiUrl = this.baseUrl + '/get?warehouseId=' + warehouseId;
    return this.http.get<Warehouse>(apiUrl);
  }

  addWarehouseItem(warehouseItem: WarehouseItem, warehouseId: string): Observable<any> {
    const apiUrl = this.baseUrl + '/inventory/add?warehouseId=' + warehouseId;
    return this.http.put(apiUrl, warehouseItem);
  }

  deleteWarehouseItem(warehouseId: string, warehouseItemId: number): Observable<any> {
    const apiUrl = this.baseUrl + '/inventory/delete?warehouseId=' + warehouseId + '&itemId=' + warehouseItemId;
    return this.http.delete(apiUrl);
  }

  decrementWarehouseItem(warehouseId: string, changeQuantity: ChangeQuantity): Observable<any> {
    const apiUrl = this.baseUrl + '/inventory/decrement?warehouseId=' + warehouseId;
    return this.http.put(apiUrl, changeQuantity);
  }

  incrementWarehouseItem(warehouseId: string, changeQuantity: ChangeQuantity): Observable<any> {
    const apiUrl = this.baseUrl + '/inventory/increment?warehouseId=' + warehouseId;
    return this.http.put(apiUrl, changeQuantity);
  }

  updateWarehouseItem(warehouseId: string, warehouseItem: WarehouseItem): Observable<any> {
    const apiUrl = this.baseUrl + '/inventory/update?warehouseId=' + warehouseId;
    return this.http.put(apiUrl, warehouseItem);
  }
}
