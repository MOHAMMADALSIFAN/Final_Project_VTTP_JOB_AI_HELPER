import { Injectable } from '@angular/core';
import { SwUpdate, VersionEvent } from '@angular/service-worker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { interval } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServiceWorkerUpdateService {

  constructor(
    private swUpdate: SwUpdate,
    private snackBar: MatSnackBar
  ) {
    if (this.swUpdate.isEnabled) {
      interval(6 * 60 * 60 * 1000).subscribe(() => {
        this.checkForUpdate();
      });

      // Handle version updates
      this.swUpdate.versionUpdates.subscribe((event: VersionEvent) => {
        // Only handle 'VERSION_DETECTED' and 'VERSION_READY' events
        if (event.type === 'VERSION_DETECTED') {
          console.log('New version detected:', event.version);
        } else if (event.type === 'VERSION_READY') {
          console.log('Current version:', event.currentVersion);
          console.log('Latest version:', event.latestVersion);
          this.showUpdateNotification();
        }
      });

      // Handle unrecoverable errors
      this.swUpdate.unrecoverable.subscribe(event => {
        console.error('SW unrecoverable error:', event.reason);
        this.snackBar.open(
          'An error occurred that requires reloading the application',
          'Reload',
          { duration: 10000 }
        ).onAction().subscribe(() => {
          window.location.reload();
        });
      });
    }
  }

  showUpdateNotification(): void {
    const snackBarRef = this.snackBar.open(
      'A new version of the app is available',
      'Update Now',
      { duration: 6000 }
    );

    snackBarRef.onAction().subscribe(() => {
      this.swUpdate.activateUpdate().then(() => window.location.reload());
    });
  }
  clearCache(): void {
    if (this.swUpdate.isEnabled && navigator.serviceWorker && navigator.serviceWorker.controller) {
      navigator.serviceWorker.controller.postMessage({
        action: 'clearCache'
      });
      console.log('Service worker cache clear request sent');
    }
  }
  checkForUpdate(): Promise<boolean> {
    if (!this.swUpdate.isEnabled) {
      return Promise.resolve(false);
    }
    return this.swUpdate.checkForUpdate();
  }

  initialCheckForUpdate() {
    if (this.swUpdate.isEnabled) {
      this.checkForUpdate().then(hasUpdate => {
        if (hasUpdate) {
          console.log('Update available on initial check');
        }
      }).catch(err => {
        console.error('Error checking for updates:', err);
      });
    }
  }
}