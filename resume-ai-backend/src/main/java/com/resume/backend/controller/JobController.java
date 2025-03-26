package com.resume.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.resume.backend.model.Job;
import com.resume.backend.service.JobService;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam String query, @RequestParam(required = true) String location) {
        List<Job> jobs = jobService.searchJobs(query, location);
        return ResponseEntity.ok(jobs);
    }
}
