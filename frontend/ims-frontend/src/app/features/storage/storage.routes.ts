import { Routes } from '@angular/router';

export const STORAGE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/storage-overview/storage-overview.component').then(m => m.StorageOverviewComponent)
  }
];
