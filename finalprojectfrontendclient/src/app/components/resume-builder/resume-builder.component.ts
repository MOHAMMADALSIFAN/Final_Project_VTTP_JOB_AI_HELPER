import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import {
  FormGroup,
  FormBuilder,
  Validators,
  FormArray,
  AbstractControl,
} from '@angular/forms';
import { jsPDF } from 'jspdf';
import html2canvas from 'html2canvas';
import { ResumeService } from '../../service/resume.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TelegramService } from '../../service/telegram.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-resume-builder',
  standalone: false,
  templateUrl: './resume-builder.component.html',
  styleUrl: './resume-builder.component.css',
})
export class ResumeBuilderComponent implements OnInit {
  @ViewChild('resumeContent') resumeContent!: ElementRef;

  initialStep = true;
  userDescription = '';
  loading = false;
  error = '';
  generatedResumeData: any = null;
  resumeForm: FormGroup;
  previewMode = false;

  telegramConnected = false;
  sendToTelegram = false;

  skillLevels = ['Beginner', 'Intermediate', 'Advanced', 'Expert'];

  constructor(
    private resumeService: ResumeService,
    private fb: FormBuilder,
    private telegramService: TelegramService,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private router: Router
  ) {
    this.resumeForm = this.createResumeForm();
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      if (!user || !user.authenticated) {
        console.log('User not authenticated for resume builder');
      } else {
        console.log('User authenticated:', user);
      }
    });

    this.telegramService.checkConnection().subscribe({
      next: (response) => {
        this.telegramConnected = response.connected;
        console.log('Telegram connected:', this.telegramConnected);
      },
      error: (error) => {
        console.error('Error checking Telegram connection:', error);
        this.telegramConnected = false;
      },
    });
  }

  createResumeForm(): FormGroup {
    return this.fb.group({
      personalInformation: this.fb.group({
        fullName: ['', Validators.required],
        location: [''],
        email: ['', [Validators.email]],
        phoneNumber: [''],
        gitHub: [''],
        linkedIn: [''],
      }),
      summary: ['', Validators.required],
      skills: this.fb.array([]),
      experience: this.fb.array([]),
      education: this.fb.array([]),
      certifications: this.fb.array([]),
      projects: this.fb.array([]),
      achievements: this.fb.array([]),
      languages: this.fb.array([]),
      interests: this.fb.array([]),
    });
  }

  // Form array getters
  get skillsArray(): FormArray {
    return this.resumeForm.get('skills') as FormArray;
  }

  get experienceArray(): FormArray {
    return this.resumeForm.get('experience') as FormArray;
  }

  get educationArray(): FormArray {
    return this.resumeForm.get('education') as FormArray;
  }

  get certificationsArray(): FormArray {
    return this.resumeForm.get('certifications') as FormArray;
  }

  get projectsArray(): FormArray {
    return this.resumeForm.get('projects') as FormArray;
  }

  get achievementsArray(): FormArray {
    return this.resumeForm.get('achievements') as FormArray;
  }

  get languagesArray(): FormArray {
    return this.resumeForm.get('languages') as FormArray;
  }

  get interestsArray(): FormArray {
    return this.resumeForm.get('interests') as FormArray;
  }

  // Add form group items
  addSkill(): void {
    this.skillsArray.push(
      this.fb.group({
        title: ['', Validators.required],
        level: ['Intermediate', Validators.required],
      })
    );
  }

  addExperience(): void {
    this.experienceArray.push(
      this.fb.group({
        jobTitle: ['', Validators.required],
        company: ['', Validators.required],
        location: [''],
        duration: [''],
        responsibility: [''],
      })
    );
  }

  addEducation(): void {
    this.educationArray.push(
      this.fb.group({
        degree: ['', Validators.required],
        university: ['', Validators.required],
        location: [''],
        graduationYear: [''],
      })
    );
  }

  addCertification(): void {
    this.certificationsArray.push(
      this.fb.group({
        title: ['', Validators.required],
        issuingOrganization: [''],
        year: [''],
      })
    );
  }

  addProject(): void {
    this.projectsArray.push(
      this.fb.group({
        title: ['', Validators.required],
        description: [''],
        technologiesUsed: [[]],
        githubLink: [''],
      })
    );
  }

  addAchievement(): void {
    this.achievementsArray.push(
      this.fb.group({
        title: ['', Validators.required],
        year: [''],
        extraInformation: [''],
      })
    );
  }

  addLanguage(): void {
    this.languagesArray.push(
      this.fb.group({
        name: ['', Validators.required],
      })
    );
  }

  addInterest(): void {
    this.interestsArray.push(
      this.fb.group({
        name: ['', Validators.required],
      })
    );
  }

  removeItem(array: FormArray, index: number): void {
    array.removeAt(index);
  }

  generateResumeData(): void {
    if (!this.userDescription.trim()) {
      this.error =
        'Please provide a description of your background and experience.';
      return;
    }

    this.loading = true;
    this.error = '';

    this.resumeService.generateResume(this.userDescription).subscribe({
      next: (response) => {
        console.log('Full response:', response);
        if (response && response.data) {
          this.generatedResumeData = response.data;
          this.populateFormWithGeneratedData(response.data);
        } else {
          this.error =
            'The response format was not as expected. Please try again.';
          console.error('Unexpected response format:', response);
        }

        this.initialStep = false;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to generate resume data. Please try again.';
        console.error('Error calling resume API:', error);
        this.loading = false;
      },
    });
  }

  populateFormWithGeneratedData(data: any): void {
    console.log('Populating form with data:', data);
    this.resumeForm = this.createResumeForm();

    try {
      if (data.personalInformation) {
        this.resumeForm.get('personalInformation')?.patchValue({
          fullName: data.personalInformation.fullName || '',
          location: data.personalInformation.location || '',
          email: data.personalInformation.email || '',
          phoneNumber: data.personalInformation.phoneNumber || '',
          gitHub: data.personalInformation.gitHub || '',
          linkedIn: data.personalInformation.linkedIn || '',
        });
      }

      // Summary
      if (data.summary) {
        this.resumeForm.get('summary')?.setValue(data.summary);
      }

      // Skills
      if (data.skills && data.skills.length) {
        this.skillsArray.clear();
        data.skills.forEach((skill: any) => {
          this.skillsArray.push(
            this.fb.group({
              title: [skill.title || '', Validators.required],
              level: [skill.level || 'Intermediate', Validators.required],
            })
          );
        });
      } else {
        this.addSkill();
      }

      // Experience
      if (data.experience && data.experience.length) {
        this.experienceArray.clear();
        data.experience.forEach((exp: any) => {
          this.experienceArray.push(
            this.fb.group({
              jobTitle: [exp.jobTitle || '', Validators.required],
              company: [exp.company || '', Validators.required],
              location: [exp.location || ''],
              duration: [exp.duration || ''],
              responsibility: [exp.responsibility || ''],
            })
          );
        });
      } else {
        this.addExperience();
      }

      // Education
      if (data.education && data.education.length) {
        this.educationArray.clear();
        data.education.forEach((edu: any) => {
          this.educationArray.push(
            this.fb.group({
              degree: [edu.degree || '', Validators.required],
              university: [edu.university || '', Validators.required],
              location: [edu.location || ''],
              graduationYear: [edu.graduationYear || ''],
            })
          );
        });
      } else {
        this.addEducation();
      }

      // Certifications
      if (data.certifications && data.certifications.length) {
        this.certificationsArray.clear();
        data.certifications.forEach((cert: any) => {
          this.certificationsArray.push(
            this.fb.group({
              title: [cert.title || '', Validators.required],
              issuingOrganization: [cert.issuingOrganization || ''],
              year: [cert.year || ''],
            })
          );
        });
      } else {
        this.addCertification();
      }

      // Projects
      if (data.projects && data.projects.length) {
        this.projectsArray.clear();
        data.projects.forEach((proj: any) => {
          this.projectsArray.push(
            this.fb.group({
              title: [proj.title || '', Validators.required],
              description: [proj.description || ''],
              technologiesUsed: [proj.technologiesUsed || []],
              githubLink: [proj.githubLink || ''],
            })
          );
        });
      } else {
        this.addProject();
      }

      // Languages
      if (data.languages && data.languages.length) {
        this.languagesArray.clear();
        data.languages.forEach((lang: any) => {
          this.languagesArray.push(
            this.fb.group({
              name: [lang.name || '', Validators.required],
            })
          );
        });
      } else {
        this.addLanguage();
      }

      // Interests
      if (data.interests && data.interests.length) {
        this.interestsArray.clear();
        data.interests.forEach((interest: any) => {
          this.interestsArray.push(
            this.fb.group({
              name: [interest.name || '', Validators.required],
            })
          );
        });
      } else {
        this.addInterest();
      }
    } catch (error) {
      console.error('Error populating form:', error);
      this.error = 'Error populating form with resume data. Please try again.';
    }
  }

  togglePreview(): void {
    if (this.resumeForm.valid) {
      this.previewMode = !this.previewMode;
    } else {
      this.resumeForm.markAllAsTouched();
      this.error = 'Please fill all required fields before previewing.';
    }
  }

  async downloadPdf(): Promise<void> {
    console.log('Download PDF started, sendToTelegram =', this.sendToTelegram);
    this.loading = true;

    try {
      // Get the filename
      const fileName =
        this.resumeForm.get('personalInformation.fullName')?.value +
        '_Resume.pdf';

      // Generate PDF for download using your existing method
      const pdfBlob = await this.resumeService.generatePdfFromHTML(
        this.resumeContent.nativeElement,
        fileName
      );
      console.log('PDF downloaded successfully');

      // Check if user wants to send to Telegram
      if (this.sendToTelegram) {
        console.log('Sending to Telegram is enabled');

        // Convert the PDF blob to base64
        const reader = new FileReader();
        reader.readAsDataURL(pdfBlob);
        reader.onloadend = () => {
          const base64data = reader.result as string;
          console.log('PDF converted to base64');

          // Send to Telegram
          this.resumeService.sendPdfToTelegram(base64data, fileName).subscribe({
            next: (response) => {
              console.log('Telegram API response:', response);
              alert(response.message || 'Resume sent to Telegram!');
              this.loading = false;
            },
            error: (error) => {
              console.error('Error sending to Telegram:', error);
              alert(
                'Failed to send resume to Telegram: ' +
                  (error.message || 'Unknown error')
              );
              this.loading = false;
            },
          });
        };
      } else {
        this.loading = false;
      }
    } catch (error) {
      console.error('Error in downloadPdf:', error);
      this.loading = false;
      this.error = 'Failed to generate PDF';
    }
  }

  resetForm(): void {
    this.initialStep = true;
    this.userDescription = '';
    this.generatedResumeData = null;
    this.previewMode = false;
    this.resumeForm = this.createResumeForm();
  }

  getTechnologiesString(control: AbstractControl): string {
    const technologies = control.get('technologiesUsed')?.value || [];
    return technologies.join(', ');
  }

  updateTechnologies(control: AbstractControl, event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const technologies = inputElement.value
      .split(',')
      .map((s) => s.trim())
      .filter((s) => s);
    control.get('technologiesUsed')?.setValue(technologies);
  }
}
