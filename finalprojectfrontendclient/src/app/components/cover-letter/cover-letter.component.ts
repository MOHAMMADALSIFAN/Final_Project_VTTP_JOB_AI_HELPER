import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-cover-letter',
  standalone: false,
  templateUrl: './cover-letter.component.html',
  styleUrl: './cover-letter.component.css',
})
export class CoverLetterComponent implements OnInit {
  coverLetterForm: FormGroup;
  generating = false;
  error = '';
  coverLetterData: any = null;
  showPreview = false;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.coverLetterForm = this.fb.group({
      userDescription: ['', [Validators.required, Validators.minLength(50)]],
    });
  }

  ngOnInit(): void {}

  generateCoverLetter(): void {
    if (this.coverLetterForm.invalid) {
      return;
    }

    this.generating = true;
    this.error = '';
    this.coverLetterData = null;
    this.showPreview = false;

    this.http
      .post<any>('/api/coverletter/generate', {
        userDescription: this.coverLetterForm.value.userDescription,
      })
      .subscribe({
        next: (response) => {
          this.generating = false;
          if (response && response.data) {
            this.coverLetterData = response.data;
            this.showPreview = true;
          } else {
            this.error = 'Failed to generate cover letter. Please try again.';
          }
        },
        error: (err) => {
          this.generating = false;
          this.error = 'An error occurred. Please try again later.';
          console.error('Cover letter generation error:', err);
        },
      });
  }

  togglePreview(): void {
    this.showPreview = !this.showPreview;
  }

  copyToClipboard(): void {
    if (!this.coverLetterData) return;

    const letterContent = this.formatCoverLetterForClipboard();

    navigator.clipboard
      .writeText(letterContent)
      .then(() => {
        alert('Cover letter copied to clipboard!');
      })
      .catch((err) => {
        console.error('Failed to copy text: ', err);
      });
  }

  private formatCoverLetterForClipboard(): string {
    const data = this.coverLetterData;
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
