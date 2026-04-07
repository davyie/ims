import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

export interface LoginRequest { username: string; password: string; }
export interface AuthResponse { token: string; userId: string; role: string; expiresAt: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private base = `${environment.apiBaseUrl}/users`;

  readonly currentUsername = signal<string | null>(localStorage.getItem('auth_username'));
  readonly currentUserId = signal<string | null>(localStorage.getItem('auth_user_id'));
  readonly isAuthenticated = signal<boolean>(this.hasValidToken());

  login(username: string, password: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.http.post<AuthResponse>(`${this.base}/login`, { username, password }).subscribe({
        next: res => {
          localStorage.setItem('auth_token', res.token);
          localStorage.setItem('auth_username', username);
          localStorage.setItem('auth_user_id', res.userId);
          this.currentUsername.set(username);
          this.currentUserId.set(res.userId);
          this.isAuthenticated.set(true);
          resolve();
        },
        error: err => reject(err)
      });
    });
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_username');
    localStorage.removeItem('auth_user_id');
    this.currentUsername.set(null);
    this.currentUserId.set(null);
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
