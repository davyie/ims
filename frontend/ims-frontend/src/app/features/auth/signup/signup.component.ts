import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NotificationService } from '../../../core/services/notification.service';
import { environment } from '../../../../environments/environment';

// Map signup codes → backend UserRole values.
// Change these codes to whatever you want to distribute.
const SIGNUP_CODES: Record<string, string> = {
  'ADMIN-2024':   'ADMIN',
  'WAREHOUSE-01': 'WAREHOUSE_MANAGER',
  'MARKET-01':    'MARKET_MANAGER',
  'VIEWER-01':    'VIEWER',
};

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirm  = control.get('confirmPassword')?.value;
  return password && confirm && password !== confirm ? { passwordMismatch: true } : null;
}

function signupCodeValidator(control: AbstractControl): ValidationErrors | null {
  const code = (control.value ?? '').trim().toUpperCase();
  return SIGNUP_CODES[code] ? null : { invalidCode: true };
}

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  private fb     = inject(FormBuilder);
  private http   = inject(HttpClient);
  private router = inject(Router);
  private notify = inject(NotificationService);

  loading     = signal(false);
  hidePassword = signal(true);
  hideConfirm  = signal(true);

  form = this.fb.group({
    username:        ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    email:           ['', [Validators.required, Validators.email]],
    signupCode:      ['', [Validators.required, signupCodeValidator]],
    password:        ['', [Validators.required, Validators.minLength(12)]],
    confirmPassword: ['', Validators.required],
  }, { validators: passwordMatchValidator });

  roleLabel(): string {
    const code = (this.form.get('signupCode')?.value ?? '').trim().toUpperCase();
    const role = SIGNUP_CODES[code];
    const labels: Record<string, string> = {
      ADMIN:             'Admin',
      WAREHOUSE_MANAGER: 'Warehouse Manager',
      MARKET_MANAGER:    'Market Manager',
      VIEWER:            'Viewer',
    };
    return role ? labels[role] : '';
  }

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    const { username, email, password, signupCode } = this.form.getRawValue();
    const role = SIGNUP_CODES[(signupCode ?? '').trim().toUpperCase()];
    try {
      await new Promise<void>((resolve, reject) => {
        this.http.post(`${environment.apiBaseUrl}/users`, {
          username, email, password, role
        }).subscribe({ next: () => resolve(), error: err => reject(err) });
      });
      this.notify.success(`Account created! Welcome, ${username}`);
      this.router.navigate(['/login']);
    } catch (err: any) {
      const msg = err?.error?.message ?? 'Registration failed. Please try again.';
      this.notify.error('Sign up failed', msg);
    } finally {
      this.loading.set(false);
    }
  }
}
