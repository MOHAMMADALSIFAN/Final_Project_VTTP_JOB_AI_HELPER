import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CoverLetterService {
  private apiUrl = '/api/coverletter';

  constructor(private http: HttpClient) {}

  /**
   * Generate a cover letter based on user description
   */
  generateCoverLetter(userDescription: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/generate`, { userDescription });
  }

  /**
   * Helper function to format the cover letter data as a string
   */
  formatCoverLetterAsText(data: any): string {
    if (!data) return '';

    let content = '';
    content += `${data.senderInfo.fullName}\n`;
    content += `${data.senderInfo.email}\n`;
    content += `${data.senderInfo.phoneNumber}\n`;
    content += `${data.senderInfo.address}\n\n`;
    content += `${data.letterInfo.date}\n\n`;
    content += `${data.letterInfo.recipientName}\n`;
    content += `${data.letterInfo.recipientTitle}\n`;
    content += `${data.letterInfo.companyName}\n`;
    content += `${data.letterInfo.companyAddress}\n\n`;
    content += `${data.greeting}\n\n`;
    content += `${data.openingParagraph}\n\n`;
    data.bodyParagraphs.forEach((para: any) => {
      content += `${para.content}\n\n`;
    });
    content += `${data.valueProposition}\n\n`;
    content += `${data.enthusiasm}\n\n`;
    content += `${data.callToAction}\n\n`;
    content += `${data.closing}\n`;
    content += `${data.senderInfo.fullName}`;

    return content;
  }
}
