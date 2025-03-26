import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface ApplyOption {
  title: string;
  link: string;
}

export interface DetectedExtensions {
  postedAt: string;
  workFromHome?: boolean;
  scheduleType?: string;
  qualifications?: string;
}

export interface Job {
  // Backend properties
  id?: number;
  title: string;
  company?: string;  // Changed to match backend
  location: string;
  description: string;
  salary?: string;
  requirements?: string[];
  postDate?: string;
  applicationUrl?: string;
  
  // Additional properties from API response
  via?: string;
  shareLink?: string;
  thumbnail?: string;
  extensions?: string[];
  detectedExtensions?: DetectedExtensions;
  applyOptions?: ApplyOption[];
  jobId?: string;
}


@Injectable({
  providedIn: 'root'
})
export class JobService {
  private apiUrl = '/api/jobs';

  constructor(private http: HttpClient) { }

  searchJobs(query: string, location?: string): Observable<Job[]> {
    let params = new HttpParams().set('query', query);
    
    if (location && location.trim() !== '') {
      params = params.set('location', location);
    }
    
    return this.http.get<Job[]>(`${this.apiUrl}/search`, { params });
  }
}
