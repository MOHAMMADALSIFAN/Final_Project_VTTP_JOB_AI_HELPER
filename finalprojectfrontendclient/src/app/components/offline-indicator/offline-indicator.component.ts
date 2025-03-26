import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-offline-indicator',
  template: `
    <div *ngIf="offline" class="offline-banner">
      <mat-icon>cloud_off</mat-icon>
      <span>You are currently offline. Some features may be unavailable.</span>
    </div>
  `,
  styles: [`
    .offline-banner {
      background-color: #f44336;
      color: white;
      padding: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 9999;
    }
    mat-icon {
      margin-right: 8px;
    }
  `],
  standalone:false
})
export class OfflineIndicatorComponent implements OnInit {
  offline = false;

  ngOnInit(): void {
    this.offline = !navigator.onLine;
    window.addEventListener('online', () => {
      this.offline = false;
    });
    window.addEventListener('offline', () => {
      this.offline = true;
    });
  }
}