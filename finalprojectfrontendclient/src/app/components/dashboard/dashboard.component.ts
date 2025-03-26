import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { BackgroundService } from '../../service/background.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy{

  showOfflineIndicator = false;
  features = [
    {
      title: 'Resume Builder',
      description: 'Create a professional resume with AI assistance',
      icon: 'description',
      route: '/resume-builder'
    },
    {
      title: 'Email Generator',
      description: 'Generate professional email responses',
      icon: 'email',
      route: '/email-generator'
    },
    {
      title: 'Job Search',
      description: 'Find job opportunities matching your skills',
      icon: 'work',
      route: '/job-search'
    },
    {
      title: 'Cover Letter Generator',
      description: 'Create tailored cover letters instantly',
      icon: 'article',
      route: '/cover-letter'
    },
    {
      title: 'Telegram',
      description: 'Connect to telegram to download the PDF',
      icon: 'article',
      route: '/telegram'
    }
  ];
  
  constructor(private router: Router,private backgroundService: BackgroundService) {}
  
  ngOnInit() {
    this.checkNetworkStatus();
  }
  ngOnDestroy() {
    this.backgroundService.changeBackground('/dashboard');
  }
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
  
  private checkNetworkStatus() {
    this.showOfflineIndicator = !navigator.onLine;
    
    window.addEventListener('online', () => {
      this.showOfflineIndicator = false;
    });
    
    window.addEventListener('offline', () => {
      this.showOfflineIndicator = true;
    });
  }
}