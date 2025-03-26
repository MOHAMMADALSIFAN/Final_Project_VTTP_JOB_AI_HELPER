// src/app/services/telegram.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, of, timeout } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TelegramService {
  private apiUrl = '/api/telegram';
  private resumeapi = '/api/v1/resume';

  constructor(private http: HttpClient) { }

  getQrCode(): Observable<any> {
    const options = { withCredentials: true };
    return this.http.get<any>(`${this.apiUrl}/qr-code`, options)
      .pipe(
        timeout(15000),  // Set a longer timeout
        catchError(error => {
          console.error('Error getting QR code:', error);
          return of({ error: 'Failed to get QR code' });
        })
      );
  }

  checkConnection(): Observable<any> {
    const options = { withCredentials: true };
    return this.http.get<any>(`${this.apiUrl}/check-connection`, options)
      .pipe(
        timeout(15000),  // Set a longer timeout
        catchError(error => {
          console.error('Error checking connection:', error);
          return of({ error: 'Failed to check connection' });
        })
      );
  }

  sendPdfToTelegram(pdfData: string, fileName: string): Observable<any> {
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    };

    // Log the request for debugging
    console.log(`Sending PDF to Telegram: ${fileName}`);

    return this.http.post<any>(`${this.resumeapi}/send-to-telegram`, {
      pdfData,
      fileName
    }, options).pipe(
      timeout(30000),  // Set a longer timeout for large files
      catchError(error => {
        console.error('Error sending PDF:', error);
        return of({ 
          success: false,
          error: 'Failed to send PDF to Telegram. Please check your connection and try again.'
        });
      })
    );
  }
}