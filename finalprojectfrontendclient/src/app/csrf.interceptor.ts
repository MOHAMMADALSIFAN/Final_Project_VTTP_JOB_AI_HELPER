// csrf.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpXsrfTokenExtractor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class CsrfInterceptor implements HttpInterceptor {
  constructor(private tokenExtractor: HttpXsrfTokenExtractor) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Skip for GET, HEAD, OPTIONS, TRACE
    if (request.method === 'GET' || request.method === 'HEAD' || 
        request.method === 'OPTIONS' || request.method === 'TRACE') {
      return next.handle(request);
    }
    
    // Get the token from the cookie
    const csrfToken = this.tokenExtractor.getToken();
    
    // If token exists, clone the request and add the token
    if (csrfToken !== null) {
      request = request.clone({
        setHeaders: {
          'X-XSRF-TOKEN': csrfToken
        }
      });
    }
    
    return next.handle(request);
  }
}