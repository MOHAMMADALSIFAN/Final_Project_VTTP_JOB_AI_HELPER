import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;
  currentUser: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user && user.authenticated;
      this.currentUser = user;
    });
  }

  navigateTo(route: string): void {
    console.log('Navigating to:', route);
    // Use navigateByUrl instead of navigate for more reliable navigation
    this.router.navigateByUrl(route);
  }

  logout(): void {
    this.authService.logout().subscribe();
  }
}
