import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './service/auth.service';
import { BackgroundService } from './service/background.service';
import { ServiceWorkerUpdateService } from './service/service-worker-update.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  currentYear = new Date().getFullYear();
  isLoggedIn = false;
  currentUser: any;
  currentBackground: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private backgroundService: BackgroundService,
    private swUpdateService: ServiceWorkerUpdateService
  ) {}

  ngOnInit(): void {
    // Subscribe to authentication state changes
    this.authService.currentUser$.subscribe((user) => {
      this.isLoggedIn = !!user && user.authenticated;
      this.currentUser = user;
    });
    // Listen for route changes to update background
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.backgroundService.changeBackground(event.url);
      });

    // Subscribe to background changes
    this.backgroundService.currentBackground.subscribe((background) => {
      this.currentBackground = background;
      console.log('Current background path:', background);
    });
  }

  logout(): void {
    this.authService.logout().subscribe();
  }
}
