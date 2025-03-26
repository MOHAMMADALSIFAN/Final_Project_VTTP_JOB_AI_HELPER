package com.resume.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private  ObjectMapper objectMapper;

    @Value("${serpapi.base.url}")
    private String baseUrl;

    @Value("${serpapi.api.key}")
    private String apiKey;

    public List<Job> searchJobs(String query, String location) {
        List<Job> allJobs = new ArrayList<>();
        String nextPageToken = null;

        do {
            String url = baseUrl +
                    "?engine=google_jobs" +
                    "&q=" + query +
                    (location != null && !location.isEmpty() ? "&location=" + location : "") +
                    "&api_key=" + apiKey +
                    (nextPageToken != null ? "&next_page_token=" + nextPageToken : "");

            System.out.println("Request URL: " + url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonData = response.getBody();

            System.out.println("Response status: " + response.getStatusCode());

            try {
                JsonNode rootNode = objectMapper.readTree(jsonData);
                JsonNode jobsResults = rootNode.get("jobs_results");

                if (jobsResults != null && jobsResults.isArray()) {
                    for (JsonNode jobNode : jobsResults) {
                        Job job = new Job();
                        job.setTitle(getTextValue(jobNode, "title"));
                        job.setCompanyName(getTextValue(jobNode, "company_name"));
                        job.setLocation(getTextValue(jobNode, "location"));
                        job.setVia(getTextValue(jobNode, "via"));
                        job.setDescription(getTextValue(jobNode, "description"));
                        job.setShareLink(getTextValue(jobNode, "share_link"));
                        job.setThumbnail(getTextValue(jobNode, "thumbnail"));
                        job.setJobId(getTextValue(jobNode, "job_id"));

                        // Parse extensions
                        if (jobNode.has("extensions") && jobNode.get("extensions").isArray()) {
                            List<String> extensions = new ArrayList<>();
                            for (JsonNode ext : jobNode.get("extensions")) {
                                extensions.add(ext.asText());
                            }
                            job.setExtensions(extensions);
                        }

                        // Parse detected_extensions
                        if (jobNode.has("detected_extensions")) {
                            JsonNode extNode = jobNode.get("detected_extensions");
                            Job.DetectedExtensions detectedExt = new Job.DetectedExtensions();

                            detectedExt.setPostedAt(getTextValue(extNode, "posted_at"));
                            if (extNode.has("work_from_home")) {
                                detectedExt.setWorkFromHome(extNode.get("work_from_home").asBoolean());
                            }
                            detectedExt.setScheduleType(getTextValue(extNode, "schedule_type"));
                            detectedExt.setQualifications(getTextValue(extNode, "qualifications"));

                            job.setDetectedExtensions(detectedExt);
                        }

                        // Parse apply_options
                        if (jobNode.has("apply_options") && jobNode.get("apply_options").isArray()) {
                            List<Job.ApplyOption> applyOptions = new ArrayList<>();
                            for (JsonNode optNode : jobNode.get("apply_options")) {
                                Job.ApplyOption option = new Job.ApplyOption();
                                option.setTitle(getTextValue(optNode, "title"));
                                option.setLink(getTextValue(optNode, "link"));
                                applyOptions.add(option);
                            }
                            job.setApplyOptions(applyOptions);
                        }

                        allJobs.add(job);
                    }
                }

                // Get the next page token for pagination
                nextPageToken = rootNode.has("serpapi_pagination") &&
                        rootNode.get("serpapi_pagination").has("next_page_token") ?
                        rootNode.get("serpapi_pagination").get("next_page_token").asText() : null;

            } catch (Exception e) {
                System.err.println("Error parsing job results: " + e.getMessage());
                e.printStackTrace();
                break;  // Stop fetching if there's an error
            }

        } while (nextPageToken != null); // Continue fetching until there are no more pages

        return allJobs;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return "";
    }
}
