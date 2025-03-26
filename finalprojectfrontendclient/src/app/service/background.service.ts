import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BackgroundService {
  private backgroundSource = new BehaviorSubject<string>(
    'assets/images/background.jpg'
  );
  currentBackground = this.backgroundSource.asObservable();
  private backgroundMap: { [key: string]: string } = {
    login: 'assets/images/background.jpg',
    signup: 'assets/images/background.jpg',
    resumeai: 'assets/images/background.jpg',
    emailai: 'assets/images/background.jpg',
    dashboard: 'assets/images/download.jpeg',
    'email-history': 'assets/images/background.jpeg',
    'cover-letter': 'assets/images/download.jpeg',
  };

  constructor() {}

  changeBackground(route: string) {
    const key = route.split('/')[1];
    const backgroundPath =
      this.backgroundMap[key] || '../assets/images/background.jpg';
    this.backgroundSource.next(backgroundPath);
  }
}
