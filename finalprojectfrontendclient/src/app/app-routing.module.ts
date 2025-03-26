import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { AuthGuard } from './service/auth.guard';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ResumeBuilderComponent } from './components/resume-builder/resume-builder.component';
import { EmailaiComponent } from './components/emailai/emailai.component';
import { JobComponent } from './components/job/job.component';
import { CoverLetterComponent } from './components/cover-letter/cover-letter.component';
import { TelegramIntegrationComponent } from './components/telegram-integration/telegram-integration.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'oauth2/callback', component: LoginComponent }, 
  { 
    path: 'dashboard', 
    component: DashboardComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'resume-builder', 
    component: ResumeBuilderComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'email-generator', 
    component: EmailaiComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'job-search', 
    component: JobComponent, 
    canActivate: [AuthGuard] 
  },
  {
    path: 'cover-letter',
    component: CoverLetterComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'telegram',
    component: TelegramIntegrationComponent,
    canActivate: [AuthGuard]
  },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
