import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { EmailHistory, EmailService } from '../../service/email.service';

@Component({
  selector: 'app-email-history',
  standalone: false,
  templateUrl: './email-history.component.html',
  styleUrl: './email-history.component.css'
})
export class EmailHistoryComponent implements OnInit {
  emailHistories: EmailHistory[] = [];
  loading = false;
  error = '';
  searchTerm = '';

  @Output() selectHistory = new EventEmitter<EmailHistory>();

  constructor(private emailService: EmailService) { }

  ngOnInit(): void {
    this.loadEmailHistory();
  }

  loadEmailHistory(): void {
    this.loading = true;
    this.emailService.getEmailHistory().subscribe({
      next: (histories) => {
        this.emailHistories = histories;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load email history.';
        console.error(error);
        this.loading = false;
      }
    });
  }

  search(): void {
    if (!this.searchTerm.trim()) {
      this.loadEmailHistory();
      return;
    }

    this.loading = true;
    this.emailService.searchEmailHistory(this.searchTerm).subscribe({
      next: (histories) => {
        this.emailHistories = histories;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to search email history.';
        console.error(error);
        this.loading = false;
      }
    });
  }

  deleteHistory(id: string, event: Event): void {
    event.stopPropagation(); 
    if (confirm('Are you sure you want to delete this email history?')) {
      this.emailService.deleteEmailHistory(id).subscribe({
        next: () => {
          this.emailHistories = this.emailHistories.filter(h => h.id !== id);
        },
        error: (error) => {
          this.error = 'Failed to delete email history.';
          console.error(error);
        }
      });
    }
  }

  onSelectHistory(history: EmailHistory): void {
    this.selectHistory.emit(history);
  }

  getFormattedDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }

  getTruncatedContent(content: string, maxLength: number = 50): string {
    return content.length > maxLength
      ? content.substring(0, maxLength) + '...'
      : content;
  }
}
