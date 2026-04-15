import { Component, inject, ViewChild, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd, NavigationStart } from '@angular/router';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BreakpointObserver } from '@angular/cdk/layout';
import { filter } from 'rxjs/operators';
import { LoadingOverlayComponent } from '../../shared/components/loading-overlay/loading-overlay.component';
import { AuthService } from '../../core/services/auth.service';
import { LoadingService } from '../../core/services/loading.service';

interface NavItem {
  label: string;
  shortLabel: string;
  icon: string;
  route: string;
  bottomNav?: boolean;
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatSidenavModule, MatToolbarModule, MatListModule,
    MatIconModule, MatButtonModule, MatTooltipModule,
    LoadingOverlayComponent,
  ],
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.scss']
})
export class ShellComponent implements OnInit {
  @ViewChild('sidenav') sidenav!: MatSidenav;

  private breakpoint = inject(BreakpointObserver);
  private router = inject(Router);
  auth = inject(AuthService);
  private loading = inject(LoadingService);

  currentUrl = signal(this.router.url);
  collapsed = signal(false);

  sidenavWidth = computed(() => this.collapsed() ? '64px' : '240px');

  navItems: NavItem[] = [
    { label: 'Home',       shortLabel: 'Home',    icon: 'home',         route: '/dashboard',     bottomNav: true },
    { label: 'Items',      shortLabel: 'Items',   icon: 'inventory_2',  route: '/items',          bottomNav: true },
    { label: 'Markets',    shortLabel: 'Markets', icon: 'storefront',   route: '/markets',        bottomNav: true },
    { label: 'Storage',    shortLabel: 'Storage', icon: 'warehouse',    route: '/storage',        bottomNav: true },
    { label: 'Reports',    shortLabel: 'Reports', icon: 'bar_chart',    route: '/reports',        bottomNav: false },
    { label: 'Txns',       shortLabel: 'Txns',    icon: 'receipt_long', route: '/transactions',   bottomNav: false },
    { label: 'Categories', shortLabel: 'Cats',    icon: 'label',        route: '/categories',     bottomNav: false },
  ];

  get bottomNavItems(): NavItem[] {
    return this.navItems.filter(i => i.bottomNav);
  }

  ngOnInit(): void {
    this.router.events.pipe(filter(e => e instanceof NavigationStart)).subscribe(() => {
      // Reset any stuck loading counter when a new navigation begins.
      // This prevents a hung request from one page blocking the next page's UI.
      this.loading.reset();
    });
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe((e: NavigationEnd) => {
      this.currentUrl.set(e.urlAfterRedirects);
    });
  }

  get currentPageLabel(): string {
    const url = this.currentUrl();
    const item = this.navItems.find(i => url.startsWith(i.route));
    return item ? item.label : 'IMS';
  }

  get isMobile(): boolean {
    return this.breakpoint.isMatched('(max-width: 767px)');
  }

  get sidenavMode(): 'over' | 'push' | 'side' {
    return this.isMobile ? 'over' : 'side';
  }

  get sidenavOpened(): boolean {
    return !this.isMobile;
  }

  toggleSidenav(): void {
    this.sidenav?.toggle();
  }

  toggleCollapsed(): void {
    this.collapsed.update(v => !v);
  }
}
