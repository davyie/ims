import { Routes } from '@angular/router';

export const MARKETS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/market-list/market-list.component').then(m => m.MarketListComponent)
  },
  {
    path: 'new',
    loadComponent: () => import('./components/market-form/market-form.component').then(m => m.MarketFormComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./components/market-detail/market-detail.component').then(m => m.MarketDetailComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./components/market-form/market-form.component').then(m => m.MarketFormComponent)
  },
  {
    path: ':id/items/add',
    loadComponent: () => import('../market-stock/components/shift-items/shift-items.component').then(m => m.ShiftItemsComponent)
  },
  {
    path: ':id/items/:itemId',
    loadComponent: () => import('../market-stock/components/market-item-detail/market-item-detail.component').then(m => m.MarketItemDetailComponent)
  },
  {
    path: ':id/summary',
    loadComponent: () => import('../reports/components/market-summary/market-summary.component').then(m => m.MarketSummaryComponent)
  },
];
