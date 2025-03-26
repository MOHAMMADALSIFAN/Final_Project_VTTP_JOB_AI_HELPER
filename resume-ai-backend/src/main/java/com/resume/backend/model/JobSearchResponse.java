package com.resume.backend.model;

import java.util.List;

public class JobSearchResponse {
  private List<Job> jobsResults;

  public JobSearchResponse() {}

  public JobSearchResponse(List<Job> jobsResults) {
      this.jobsResults = jobsResults;
  }

  public List<Job> getJobsResults() { return jobsResults; }
  public void setJobsResults(List<Job> jobsResults) { this.jobsResults = jobsResults; }
}
