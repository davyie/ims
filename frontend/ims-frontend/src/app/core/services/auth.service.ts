import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

export interface LoginRequest { email: string; password: string; }
export interface RegisterRequest { email: string; password: string; registrationCode: string; }
export interface AuthResponse { token: string; tokenType: string; expiresIn: number; email: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private base = `${environment.apiBaseUrl}/auth`;

  readonly currentEmail = signal<string | null>(localStorage.getItem('auth_email'));
  readonly isAuthenticated = signal<boolean>(this.hasValidToken());

  login(email: string, password: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.http.post<AuthResponse>(`${this.base}/login`, { email, password }).subscribe({
        next: res => {
          localStorage.setItem('auth_token', res.token);
          localStorage.setItem('auth_email', res.email);
          this.currentEmail.set(res.email);
          this.isAuthenticated.set(true);
          resolve();
        },
        error: err => reject(err)
      });
    });
  }

  register(email: string, password: string, registrationCode: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.http.post<AuthResponse>(`${this.base}/register`, { email, password, registrationCode }).subscribe({
        next: res => {
          localStorage.setItem('auth_token', res.token);
          localStorage.setItem('auth_email', res.email);
          this.currentEmail.set(res.email);
          this.isAuthenticated.set(true);
          resolve();
        },
        error: err => reject(err)
      });
    });
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_email');
    this.currentEmail.set(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  private hasValidToken(): boolean {
    const token = localStorage.getItem('auth_token');
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }
}
