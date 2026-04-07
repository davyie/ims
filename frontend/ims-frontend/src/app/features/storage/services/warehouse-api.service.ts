import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  Warehouse, WarehouseStock, Page,
  CreateWarehouseRequest, AddStockRequest, RemoveStockRequest, WarehouseAdjustStockRequest
} from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class WarehouseApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/warehouses`;

  listWarehouses(page = 0, size = 50): Observable<Page<Warehouse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Warehouse>>(this.base, { params });
  }

  getWarehouse(warehouseId: string): Observable<Warehouse> {
    return this.http.get<Warehouse>(`${this.base}/${warehouseId}`);
  }

  createWarehouse(req: CreateWarehouseRequest): Observable<Warehouse> {
    return this.http.post<Warehouse>(this.base, req);
  }

  listStock(warehouseId: string, page = 0, size = 100): Observable<Page<WarehouseStock>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<WarehouseStock>>(`${this.base}/${warehouseId}/stock`, { params });
  }

  addStock(warehouseId: string, req: AddStockRequest): Observable<WarehouseStock> {
    return this.http.post<WarehouseStock>(`${this.base}/${warehouseId}/stock/add`, req);
  }

  removeStock(warehouseId: string, req: RemoveStockRequest): Observable<WarehouseStock> {
    return this.http.post<WarehouseStock>(`${this.base}/${warehouseId}/stock/remove`, req);
  }

  adjustStock(warehouseId: string, req: WarehouseAdjustStockRequest): Observable<WarehouseStock> {
    return this.http.put<WarehouseStock>(`${this.base}/${warehouseId}/stock/adjust`, req);
  }
}
