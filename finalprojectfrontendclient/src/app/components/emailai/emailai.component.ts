import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { EmailHistory, EmailService } from '../../service/email.service';



@Component({
  selector: 'app-emailai',
  standalone: false,
  templateUrl: './emailai.component.html',
  styleUrl: './emailai.component.css'
})
export class EmailaiComponent {
  emailForm: FormGroup;
  generatedReply: string = '';
  loading: boolean = false;
  error: string = '';
  isMobileView: boolean = false;
  showSidebar: boolean = true;

  toneOptions = [
    { value: 'professional', label: 'Professional' },
    { value: 'casual', label: 'Casual' },
    { value: 'friendly', label: 'Friendly' }
  ];

  constructor(
    private fb: FormBuilder,
    private emailService: EmailService
  ) {
    this.emailForm = this.fb.group({
      emailContent: ['', Validators.required],
      tone: ['professional']
    });

    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
  }

  checkScreenSize() {
    this.isMobileView = window.innerWidth < 992;
    this.showSidebar = !this.isMobileView;
  }

  toggleSidebar() {
    this.showSidebar = !this.showSidebar;
  }

  generateReply() {
    if (this.emailForm.valid) {
      this.loading = true;
      this.error = '';

      const payload = {
        emailContent: this.emailForm.value.emailContent,
        tone: this.emailForm.value.tone
      };

      this.emailService.generateEmailReply(payload).subscribe({
        next: (response) => {
          this.generatedReply = response;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to generate email reply. Please try again.';
          console.error(error);
          this.loading = false;
        }
      });
    }
  }

  copyToClipboard() {
    navigator.clipboard.writeText(this.generatedReply)
      .then(() => {
        alert('Copied to clipboard!');
      })
      .catch(err => {
        console.error('Failed to copy text: ', err);
      });
  }

  onSelectHistory(history: EmailHistory) {
    this.emailForm.patchValue({
      emailContent: history.originalContent,
      tone: history.tone
    });
    

    this.generatedReply = history.generatedReply;
    
    if (this.isMobileView) {
      this.showSidebar = false;
    }
  }
}