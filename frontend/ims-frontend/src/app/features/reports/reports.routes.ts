import { Routes } from '@angular/router';

export const REPORTS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/all-markets-report/all-markets-report.component').then(m => m.AllMarketsReportComponent)
  }
];
