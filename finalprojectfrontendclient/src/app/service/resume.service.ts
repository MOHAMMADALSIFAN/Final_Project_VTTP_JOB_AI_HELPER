import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';
import { TelegramService } from './telegram.service';

@Injectable({
  providedIn: 'root',
})
export class ResumeService {
  private apiUrl = '/api/v1/resume';

  constructor(
    private http: HttpClient,
    private telegramService: TelegramService
  ) {}

  generateResume(userDescription: string): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/generate`,
      { userDescription },
      { withCredentials: true }
    );
  }

  sendPdfToTelegram(pdfData: string, fileName: string): Observable<any> {
    return this.http.post<any>(
      `/api/v1/resume/send-to-telegram`,
      { pdfData, fileName },
      { withCredentials: true }
    );
  }



  async generatePdfFromHTML(
    element: HTMLElement,
    fileName: string = 'resume.pdf'
  ): Promise<Blob> {
    try {
      // Create a clone of the element to avoid modifying the original
      const clone = element.cloneNode(true) as HTMLElement;

      // Add PDF-specific styling
      clone.classList.add('pdf-export');

      // Find and remove elements that should not appear in PDF
      const buttonsToRemove = clone.querySelectorAll(
        '.preview-actions, button, mat-spinner'
      );
      buttonsToRemove.forEach((btn) => btn.parentNode?.removeChild(btn));
      const emailIcons = clone.querySelectorAll(
        '.resume-contact span:has(mat-icon[fonticon="email"])'
      );
      emailIcons.forEach((icon) => {
        const iconEl = icon.querySelector('mat-icon');
        if (iconEl) iconEl.textContent = 'Email: ';
      });

      const phoneIcons = clone.querySelectorAll(
        '.resume-contact span:has(mat-icon[fonticon="phone"])'
      );
      phoneIcons.forEach((icon) => {
        const iconEl = icon.querySelector('mat-icon');
        if (iconEl) iconEl.textContent = 'Phone: ';
      });

      clone.style.position = 'absolute';
      clone.style.left = '-9999px';
      clone.style.background = 'white';
      document.body.appendChild(clone);

      const options = {
        useCORS: true,
        logging: false,
        allowTaint: true,
        backgroundColor: '#FFFFFF',
        scale: 2,
      } as any; // Type assertion

      const canvas = await html2canvas(clone, options);

      // Remove the clone from document
      document.body.removeChild(clone);

      // Calculate dimensions to fit A4 page
      const imgWidth = 210; // A4 width in mm (210mm)
      const pageHeight = 297; // A4 height in mm (297mm)
      const imgHeight = (canvas.height * imgWidth) / canvas.width;

      // Create PDF with A4 dimensions
      const pdf = new jsPDF('p', 'mm', 'a4');

      // Add image to PDF
      pdf.addImage(
        canvas.toDataURL('image/jpeg', 1.0),
        'JPEG',
        0,
        0,
        imgWidth,
        imgHeight
      );

      // If content is longer than one page, add new pages
      let heightLeft = imgHeight;
      let position = 0;

      while (heightLeft > pageHeight) {
        position = heightLeft - pageHeight;
        heightLeft -= pageHeight;

        // Add new page
        pdf.addPage();

        // Add content that didn't fit on the previous page
        pdf.addImage(
          canvas.toDataURL('image/jpeg', 1.0),
          'JPEG',
          0,
          -position,
          imgWidth,
          imgHeight
        );
      }

      // Save the PDF
      pdf.save(fileName);

      // Return the PDF as a blob
      return pdf.output('blob');
    } catch (error) {
      console.error('Error generating PDF:', error);
      throw error;
    }
  }
}
