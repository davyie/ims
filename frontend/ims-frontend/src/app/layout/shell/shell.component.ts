import { Component, inject, ViewChild, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BreakpointObserver } from '@angular/cdk/layout';
import { filter } from 'rxjs/operators';
import { LoadingOverlayComponent } from '../../shared/components/loading-overlay/loading-overlay.component';

interface NavItem {
  label: string;
  shortLabel: string;
  icon: string;
  route: string;
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

  currentUrl = signal(this.router.url);

  navItems: NavItem[] = [
    { label: 'Dashboard', shortLabel: 'Home',   icon: 'dashboard',    route: '/dashboard' },
    { label: 'Items',     shortLabel: 'Items',   icon: 'inventory_2',  route: '/items' },
    { label: 'Markets',   shortLabel: 'Markets', icon: 'storefront',   route: '/markets' },
    { label: 'Storage',   shortLabel: 'Storage', icon: 'warehouse',    route: '/storage' },
    { label: 'Reports',   shortLabel: 'Reports', icon: 'bar_chart',    route: '/reports' },
    { label: 'Txns',      shortLabel: 'Txns',    icon: 'receipt_long', route: '/transactions' },
  ];

  ngOnInit(): void {
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
}
