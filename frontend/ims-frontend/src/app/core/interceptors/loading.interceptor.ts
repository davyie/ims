import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize, timeout, catchError, throwError } from 'rxjs';
import { LoadingService } from '../services/loading.service';

const REQUEST_TIMEOUT_MS = 15_000;

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingService = inject(LoadingService);
  loadingService.increment();
  return next(req).pipe(
    timeout(REQUEST_TIMEOUT_MS),
    catchError(err => throwError(() => err)), // pass error through; finalize still runs
    finalize(() => loadingService.decrement())
  );
};
