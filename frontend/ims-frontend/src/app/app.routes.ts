import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell/shell.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: '',
    component: ShellComponent,
    children: [
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES)
      },
      {
        path: 'items',
        loadChildren: () => import('./features/items/items.routes').then(m => m.ITEMS_ROUTES)
      },
      {
        path: 'markets',
        loadChildren: () => import('./features/markets/markets.routes').then(m => m.MARKETS_ROUTES)
      },
      {
        path: 'storage',
        loadChildren: () => import('./features/storage/storage.routes').then(m => m.STORAGE_ROUTES)
      },
      {
        path: 'reports',
        loadChildren: () => import('./features/reports/reports.routes').then(m => m.REPORTS_ROUTES)
      },
      {
        path: 'transactions',
        loadChildren: () => import('./features/transactions/transactions.routes').then(m => m.TRANSACTIONS_ROUTES)
      },
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
