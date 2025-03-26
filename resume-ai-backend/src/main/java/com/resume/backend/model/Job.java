    package com.resume.backend.model;

    import java.util.List;

    public class Job {
    private String title;
    private String companyName;
    private String location;
    private String via;
    private String shareLink;
    private String thumbnail;
    private List<String> extensions;
    private DetectedExtensions detectedExtensions;
    private String description;
    private List<ApplyOption> applyOptions;
    private String jobId;

    public Job() {}

    public Job(String title, String companyName, String location, String via, String shareLink,
                String thumbnail, List<String> extensions, DetectedExtensions detectedExtensions,
                String description, List<ApplyOption> applyOptions, String jobId) {
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.via = via;
        this.shareLink = shareLink;
        this.thumbnail = thumbnail;
        this.extensions = extensions;
        this.detectedExtensions = detectedExtensions;
        this.description = description;
        this.applyOptions = applyOptions;
        this.jobId = jobId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }
    
    public String getShareLink() { return shareLink; }
    public void setShareLink(String shareLink) { this.shareLink = shareLink; }
    
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    
    public List<String> getExtensions() { return extensions; }
    public void setExtensions(List<String> extensions) { this.extensions = extensions; }
    
    public DetectedExtensions getDetectedExtensions() { return detectedExtensions; }
    public void setDetectedExtensions(DetectedExtensions detectedExtensions) { this.detectedExtensions = detectedExtensions; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<ApplyOption> getApplyOptions() { return applyOptions; }
    public void setApplyOptions(List<ApplyOption> applyOptions) { this.applyOptions = applyOptions; }
    
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    // Nested classes
    public static class DetectedExtensions {
        private String postedAt;
        private Boolean workFromHome;
        private String scheduleType;
        private String qualifications;

        public DetectedExtensions() {}

        public DetectedExtensions(String postedAt, Boolean workFromHome, String scheduleType, String qualifications) {
            this.postedAt = postedAt;
            this.workFromHome = workFromHome;
            this.scheduleType = scheduleType;
            this.qualifications = qualifications;
        }

        public String getPostedAt() { return postedAt; }
        public void setPostedAt(String postedAt) { this.postedAt = postedAt; }

        public Boolean getWorkFromHome() { return workFromHome; }
        public void setWorkFromHome(Boolean workFromHome) { this.workFromHome = workFromHome; }

        public String getScheduleType() { return scheduleType; }
        public void setScheduleType(String scheduleType) { this.scheduleType = scheduleType; }

        public String getQualifications() { return qualifications; }
        public void setQualifications(String qualifications) { this.qualifications = qualifications; }
    }

    public static class ApplyOption {
        private String title;
        private String link;

        public ApplyOption() {}

        public ApplyOption(String title, String link) {
            this.title = title;
            this.link = link;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }
    }