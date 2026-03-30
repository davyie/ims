import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private snackBar = inject(MatSnackBar);

  success(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: ['snack-success'],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
  }

  warning(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: ['snack-warning'],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
  }

  error(message: string, detail?: string): void {
    const text = detail ? `${message}: ${detail}` : message;
    this.snackBar.open(text, 'Close', {
      duration: 7000,
      panelClass: ['snack-error'],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
  }
}
