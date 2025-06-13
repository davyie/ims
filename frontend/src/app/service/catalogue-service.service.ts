import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Item {
  name: string,
  description: string, 
  quantity: number
}

@Injectable({
  providedIn: 'root'
})

export class CatalogueServiceService {

  constructor(private client: HttpClient) { }

  getItemsByRequest(): Observable<Item[]> {
    return this.client.get<Item[]>("http://localhost:8080/query/get/all");
  }

  postItemByRequest(item: Item): Observable<Item> {
    return this.client.post<Item>("http://localhost:8080/commands/", item);
  }
}
