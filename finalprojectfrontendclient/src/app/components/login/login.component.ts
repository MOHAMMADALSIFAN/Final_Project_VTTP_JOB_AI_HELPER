import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  hidePassword = true;
  returnUrl: string = '/dashboard';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    // First check if we're coming back from OAuth
    if (this.route.snapshot.queryParams['code'] || this.route.snapshot.queryParams['token']) {
      this.handleOAuthReturn();
      return;
    }

    // Get return url from route parameters or default to dashboard
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
    
    // Check if user is already logged in
    this.checkAuthenticationStatus();
  }

  // New method to handle OAuth return
  private handleOAuthReturn(): void {
    this.isLoading = true;
    
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.isLoading = false;
        if (user && user.authenticated) {
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
          this.router.navigateByUrl(returnUrl);
        } else {
          this.errorMessage = 'Authentication failed. Please try again.';
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error during OAuth authentication:', error);
        this.errorMessage = 'Authentication failed. Please try again.';
      }
    });
  }

  // Check authentication status with server
  private checkAuthenticationStatus(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        if (user && user.authenticated) {
          this.router.navigateByUrl(this.returnUrl);
        }
      },
      error: (error) => {
        console.error('Error checking authentication status:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      const loginData = {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password
      };

      this.authService.login(loginData).subscribe({
        next: (response: any) => {
          this.isLoading = false;
          if (response.authenticated) {
            this.router.navigateByUrl(this.returnUrl);
          } else {
            this.errorMessage = response.message || 'Login failed. Please try again.';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'An error occurred. Please try again.';
        }
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  loginWithGoogle(): void {
    const currentUrl = encodeURIComponent(window.location.href);
    window.location.href = `/oauth2/authorization/google?redirect_uri=${currentUrl}`;
  }

  navigateToSignup(): void {
    this.router.navigate(['/signup']);
  }
}