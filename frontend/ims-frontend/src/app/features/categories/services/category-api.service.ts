import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Category, CreateCategoryRequest } from '../../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class CategoryApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/categories`;

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.base);
  }

  createCategory(req: CreateCategoryRequest): Observable<Category> {
    return this.http.post<Category>(this.base, req);
  }

  deleteCategory(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
