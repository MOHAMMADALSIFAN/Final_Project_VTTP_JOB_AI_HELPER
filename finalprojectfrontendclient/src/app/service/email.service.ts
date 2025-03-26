import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface EmailHistory {
  id: string;
  originalContent: string;
  generatedReply: string;
  tone: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmailService {
  private apiUrl = '/api/email';

  constructor(private http: HttpClient) { }

  // Generate email reply
  generateEmailReply(emailData: { emailContent: string, tone: string }): Observable<string> {
    return this.http.post(`${this.apiUrl}/generate`, emailData, {
      responseType: 'text'  // Specify text response type
    });
  }

  // Get all email history
  getEmailHistory(): Observable<EmailHistory[]> {
    return this.http.get<EmailHistory[]>(`${this.apiUrl}/history`);
  }

  // Get email history by ID
  getEmailHistoryById(id: string): Observable<EmailHistory> {
    return this.http.get<EmailHistory>(`${this.apiUrl}/history/${id}`);
  }

  // Search email history
  searchEmailHistory(term: string): Observable<EmailHistory[]> {
    return this.http.get<EmailHistory[]>(`${this.apiUrl}/history/search`, {
      params: { term }
    });
  }

  // Delete email history
  deleteEmailHistory(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/history/${id}`);
  }
}