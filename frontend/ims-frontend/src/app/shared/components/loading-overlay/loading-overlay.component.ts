import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LoadingService } from '../../../core/services/loading.service';

@Component({
  selector: 'app-loading-overlay',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule],
  template: `
    <div *ngIf="loadingService.isLoading()" class="loading-overlay">
      <mat-spinner diameter="48"></mat-spinner>
    </div>
  `,
  styles: [`
    .loading-overlay {
      position: fixed; inset: 0; background: rgba(255,255,255,0.7);
      display: flex; align-items: center; justify-content: center;
      z-index: 9999;
    }
  `]
})
export class LoadingOverlayComponent {
  loadingService = inject(LoadingService);
}
