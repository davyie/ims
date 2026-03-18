import { CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  // v1: always allow access
  return true;
};
