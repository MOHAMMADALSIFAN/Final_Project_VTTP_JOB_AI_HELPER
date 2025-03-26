// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, catchError, finalize, tap } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { ServiceWorkerUpdateService } from './service-worker-update.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<any>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();
  private apiUrl = '/api/auth';

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private swUpdateService: ServiceWorkerUpdateService
  ) {
    this.checkOAuthRedirect();
  }

private checkOAuthRedirect(): void {
  if (window.location.search.includes('code=') || window.location.search.includes('token=')) {
    this.getCurrentUser().subscribe({
      next: (user) => {
        if (user && user.authenticated) {
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
          this.router.navigateByUrl(returnUrl);
        }
      }
    });
  } else {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        this.currentUserSubject.next(user);
      } catch (e) {
        console.error('Error parsing stored user', e);
        localStorage.removeItem('currentUser');
      }
    }
  }
}

  private getUserFromStorage(): any {
    try {
      const storedUser = localStorage.getItem('currentUser');
      return storedUser ? JSON.parse(storedUser) : null;
    } catch (e) {
      console.error('Error parsing stored user', e);
      localStorage.removeItem('currentUser');
      return null;
    }
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/user`).pipe(
      tap((user: any) => {
        if (user && user.authenticated) {
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(user);
        }
      }),
      catchError(error => {
        console.error('Error getting current user', error);
        return of(null);
      })
    );
  }

  getProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/profile`);
  }

  login(loginData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, loginData).pipe(
      tap((response: any) => {
        if (response && response.authenticated) {
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.currentUserSubject.next(response);
        }
      })
    );
  }

  signup(signupData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, signupData).pipe(
      tap((response: any) => {
        if (response && response.success) {
          this.currentUserSubject.next(response);
          this.router.navigate(['/dashboard']);
        }
      })
    );
  }

  logout(): Observable<any> {
    localStorage.removeItem('currentUser');
    sessionStorage.clear();
    
    this.clearAllCookies();
    this.currentUserSubject.next(null);
    
    // Clear service worker cache
    this.swUpdateService.clearCache();
    
    return this.http.get(`${this.apiUrl}/logout`).pipe(
      finalize(() => {
        this.router.navigate(['/login']);
      }),
      catchError(error => {
        console.error('Error during logout:', error);
        return of({ success: true, message: 'Logged out locally' });
      })
    );
  }

  private clearAllCookies() {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
      const eqPos = cookie.indexOf('=');
      const name = eqPos > -1 ? cookie.substr(0, eqPos).trim() : cookie.trim();
      document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/';
    }
  }

  setCurrentUser(user: any): void {
    this.currentUserSubject.next(user);
  }

  getCurrentUserValue(): any {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    const user = this.currentUserSubject.value;
    return !!user && user.authenticated;
  }
}