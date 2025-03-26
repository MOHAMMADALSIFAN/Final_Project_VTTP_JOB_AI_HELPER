// job.component.ts
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { Job, JobService } from '../../service/job.service';

@Component({
  selector: 'app-job',
  standalone: false,
  templateUrl: './job.component.html',
  styleUrls: ['./job.component.css'],
})
export class JobComponent implements OnInit {
  searchForm: FormGroup;
  jobs: Job[] = [];
  paginatedJobs: Job[] = [];
  loading = false;
  error = '';
  searchPerformed = false;

  currentPage = 0;
  pageSize = 10;

  constructor(private jobService: JobService, private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      query: [''],
      location: [''],
    });
  }

  ngOnInit(): void {
    this.setupSearch();
  }

  onPageChange(event: any) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePaginatedJobs();
  }

  updatePaginatedJobs() {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedJobs = this.jobs.slice(startIndex, endIndex);
  }

  setupSearch(): void {
    this.searchForm
      .get('query')
      ?.valueChanges.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap((query) => {
          if (!query || query.trim() === '') {
            this.jobs = [];
            this.paginatedJobs = [];
            this.searchPerformed = false;
            return of([]);
          }

          this.loading = true;
          this.searchPerformed = true;
          const location = this.searchForm.get('location')?.value;
          return this.jobService.searchJobs(query, location);
        })
      )
      .subscribe({
        next: (results) => {
          this.jobs = results;
          this.currentPage = 0;
          this.updatePaginatedJobs();
          this.loading = false;
          this.error = '';
        },
        error: (err) => {
          console.error('Error searching jobs:', err);
          this.error = 'Failed to search jobs. Please try again.';
          this.loading = false;
        },
      });
  }

  searchJobs(): void {
    const query = this.searchForm.get('query')?.value;
    const location = this.searchForm.get('location')?.value;

    if (!query || query.trim() === '') {
      return;
    }

    this.loading = true;
    this.searchPerformed = true;
    this.jobService.searchJobs(query, location).subscribe({
      next: (results) => {
        this.jobs = results;
        this.currentPage = 0;
        this.updatePaginatedJobs();
        this.loading = false;
        this.error = '';
      },
      error: (err) => {
        console.error('Error searching jobs:', err);
        this.error = 'Failed to search jobs. Please try again.';
        this.loading = false;
      },
    });
  }

  // Get the first apply link from the apply options
  getApplyLink(job: Job): string {
    if (job.applyOptions && job.applyOptions.length > 0) {
      return job.applyOptions[0].link;
    }

    return job.shareLink || '#';
  }

  handleApplyClick(event: MouseEvent, job: Job): void {
    event.preventDefault();
    event.stopPropagation();

    const applyUrl = this.getApplyLink(job);
    if (applyUrl && applyUrl !== '#') {
      window.open(applyUrl, '_blank', 'noopener,noreferrer');
    } else {
      console.warn('No apply link available for this job');
    }
  }

  // Format the posted time
  getPostedTime(job: Job): string {
    if (job.detectedExtensions && job.detectedExtensions.postedAt) {
      return job.detectedExtensions.postedAt;
    }
    return '';
  }

  // Check if job is remote
  isRemote(job: Job): boolean {
    return job.detectedExtensions?.workFromHome || false;
  }

  // Get job schedule type
  getScheduleType(job: Job): string {
    return job.detectedExtensions?.scheduleType || '';
  }
}
