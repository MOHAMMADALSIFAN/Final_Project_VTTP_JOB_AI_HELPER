import { Component, OnInit } from '@angular/core';
import { TelegramService } from '../../service/telegram.service';
import { Router } from '@angular/router';
import jsPDF from 'jspdf';
import { ResumeService } from '../../service/resume.service';

@Component({
  selector: 'app-telegram-integration',
  standalone: false,
  templateUrl: './telegram-integration.component.html',
  styleUrl: './telegram-integration.component.css',
})
export class TelegramIntegrationComponent implements OnInit {
  qrCodeImage: string | null = null;
  deepLink: string | null = null;
  loading = false;
  error = '';

  constructor(
    private telegramService: TelegramService,
    private resumeService: ResumeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.generateQrCode();
  }

  generateQrCode(): void {
    this.loading = true;
    this.error = '';

    this.telegramService.getQrCode().subscribe({
      next: (response) => {
        this.qrCodeImage = response.qrCode;
        this.deepLink = response.deepLink;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to generate QR code. Please try again.';
        this.loading = false;
        console.error('QR code generation error:', err);
      },
    });
  }

  openTelegramLink(): void {
    if (this.deepLink) {
      window.open(this.deepLink, '_blank');
    }
  }

  goBack(): void {
    window.history.back();
  }
  sendTestPdf(): void {
    // Create a simple PDF
    const doc = new jsPDF();
    doc.text('Test PDF for Telegram', 10, 10);
    const pdfData = doc.output('datauristring');

    this.loading = true;

    // Send to Telegram
    this.resumeService.sendPdfToTelegram(pdfData, 'test.pdf').subscribe({
      next: (response) => {
        console.log('Test PDF sent:', response);
        alert('Test PDF sent to Telegram!');
        this.loading = false;
      },
      error: (error) => {
        console.error('Error sending test PDF:', error);
        alert('Error sending PDF: ' + (error.message || 'Unknown error'));
        this.loading = false;
      },
    });
  }
}
