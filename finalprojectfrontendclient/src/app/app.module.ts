import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from './material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';

import { CsrfInterceptor } from './csrf.interceptor';
import { ErrorInterceptor } from './error.interceptor';
import { AuthGuard } from './service/auth.guard';
import { AuthService } from './service/auth.service';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmailaiComponent } from './components/emailai/emailai.component';
import { ResumeBuilderComponent } from './components/resume-builder/resume-builder.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { EmailService } from './service/email.service';
import { ResumeService } from './service/resume.service';
import { ServiceWorkerModule } from '@angular/service-worker';
import { OfflineIndicatorComponent } from './components/offline-indicator/offline-indicator.component';
import { JobComponent } from './components/job/job.component';
import { EmailHistoryComponent } from './components/email-history/email-history.component';
import { CoverLetterComponent } from './components/cover-letter/cover-letter.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TelegramIntegrationComponent } from './components/telegram-integration/telegram-integration.component';
import { CredentialsInterceptor } from './credentials.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    DashboardComponent,
    EmailaiComponent,
    ResumeBuilderComponent,
    NavbarComponent,
    OfflineIndicatorComponent,
    JobComponent,
    EmailHistoryComponent,
    CoverLetterComponent,
    TelegramIntegrationComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    FormsModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      registrationStrategy: 'registerWhenStable:30000',
    }),
    NgbModule,
  ],
  providers: [
    AuthService,
    AuthGuard,
    EmailService,
    ResumeService,
    { provide: HTTP_INTERCEPTORS, useClass: CredentialsInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: CsrfInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
