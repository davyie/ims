import { Routes } from '@angular/router';

export const MARKET_STOCK_ROUTES: Routes = [
  {
    path: ':marketId/items/add',
    loadComponent: () => import('./components/shift-items/shift-items.component').then(m => m.ShiftItemsComponent)
  },
  {
    path: ':marketId/items/:itemId',
    loadComponent: () => import('./components/market-item-detail/market-item-detail.component').then(m => m.MarketItemDetailComponent)
  },
];
