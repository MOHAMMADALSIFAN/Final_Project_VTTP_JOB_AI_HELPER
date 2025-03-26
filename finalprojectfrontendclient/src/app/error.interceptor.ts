// error.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('HTTP Error:', error);
        
        // Check if this is a Telegram-related endpoint
        const isTelegramEndpoint = 
          error.url?.includes('/api/telegram/') || 
          error.url?.includes('/api/v1/resume/send-to-telegram');
        
        if (error.status === 401 && !isTelegramEndpoint) {
          // Auto logout if 401 response returned from API (except for Telegram endpoints)
          console.log('Authentication error - redirecting to login');
          this.router.navigate(['/login'], { 
            queryParams: { returnUrl: this.router.url }
          });
          this.snackBar.open('Session expired. Please log in again.', 'Close', {
            duration: 5000,
            horizontalPosition: 'center',
            verticalPosition: 'bottom'
          });
        } else if (error.status === 401 && isTelegramEndpoint) {
          // Just show a message for Telegram endpoints without redirecting
          console.log('Authentication required for Telegram operations');
          this.snackBar.open('Authentication required for Telegram operations', 'Close', {
            duration: 5000
          });
        } else if (error.status === 403) {
          this.snackBar.open('You do not have permission to access this resource.', 'Close', {
            duration: 5000
          });
        } else if (error.status === 0) {
          // Connection error
          this.snackBar.open('Cannot connect to server. Please check your internet connection.', 'Close', {
            duration: 5000
          });
        } else if (error.status >= 500) {
          this.snackBar.open('Server error. Please try again later.', 'Close', {
            duration: 5000
          });
        }
        
        return throwError(() => error);
      })
    );
  }
}