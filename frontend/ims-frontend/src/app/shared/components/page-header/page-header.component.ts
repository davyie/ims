import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

export interface Breadcrumb { label: string; link?: string; }

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  template: `
    <div class="page-header">
      <nav *ngIf="breadcrumbs.length > 1" class="breadcrumbs" aria-label="breadcrumb">
        <span *ngFor="let crumb of breadcrumbs; let last = last" class="crumb">
          <a *ngIf="crumb.link && !last" [routerLink]="crumb.link">{{ crumb.label }}</a>
          <span *ngIf="!crumb.link || last">{{ crumb.label }}</span>
          <mat-icon *ngIf="!last" class="sep">chevron_right</mat-icon>
        </span>
      </nav>
      <div class="header-row">
        <h1 class="page-title">{{ title }}</h1>
        <div class="actions">
          <ng-content></ng-content>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 24px; }
    .breadcrumbs {
      display: flex; align-items: center; flex-wrap: wrap;
      font-size: 13px; color: var(--text-secondary); margin-bottom: 8px;
    }
    .crumb { display: flex; align-items: center; }
    .crumb a { color: var(--primary); text-decoration: none; }
    .crumb a:hover { text-decoration: underline; }
    .sep { font-size: 16px; width: 16px; height: 16px; color: #9E9E9E; }
    .header-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; flex-wrap: wrap; }
    .page-title { margin: 0; font-size: 24px; font-weight: 600; color: var(--text-primary); }
    .actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
    @media (max-width: 480px) {
      .page-title { font-size: 20px; }
      .header-row { gap: 8px; }
    }
  `]
})
export class PageHeaderComponent {
  @Input() title: string = '';
  @Input() breadcrumbs: Breadcrumb[] = [];
}
