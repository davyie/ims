import { Routes } from '@angular/router';

export const ITEMS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/item-list/item-list.component').then(m => m.ItemListComponent)
  },
  {
    path: 'new',
    loadComponent: () => import('./components/item-form/item-form.component').then(m => m.ItemFormComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./components/item-detail/item-detail.component').then(m => m.ItemDetailComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./components/item-form/item-form.component').then(m => m.ItemFormComponent)
  },
];
