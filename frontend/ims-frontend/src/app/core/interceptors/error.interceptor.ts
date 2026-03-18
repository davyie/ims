import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 0) {
        notificationService.error('Network error', 'Could not connect to the server');
      } else if (error.status === 404) {
        notificationService.warning('Resource not found');
      } else if (error.status === 400) {
        const message = error.error?.message || 'Invalid request';
        notificationService.error('Validation error', message);
      } else if (error.status >= 500) {
        const message = error.error?.message || error.message;
        notificationService.error('Server error', message);
      } else if (error.status === 409) {
        const message = error.error?.message || 'Conflict';
        notificationService.error('Conflict', message);
      } else {
        notificationService.error(`Error ${error.status}`, error.message);
      }
      return throwError(() => error);
    })
  );
};
