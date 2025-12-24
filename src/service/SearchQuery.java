package service;

public class SearchQuery {

    private String jobId;
    private String title;
    private String details;
    private String location;
    private String workingHours;
    private String dateTime;
    private String imagePath;
    private String vacancies;

    // optional (อาจยังไม่มีใน Job ตอนนี้)
    private String jobType;
    private String createdBy;

    public SearchQuery jobId(String v) { this.jobId = v; return this; }
    public SearchQuery title(String v) { this.title = v; return this; }
    public SearchQuery details(String v) { this.details = v; return this; }
    public SearchQuery location(String v) { this.location = v; return this; }
    public SearchQuery workingHours(String v) { this.workingHours = v; return this; }
    public SearchQuery dateTime(String v) { this.dateTime = v; return this; }
    public SearchQuery imagePath(String v) { this.imagePath = v; return this; }
    public SearchQuery vacancies(String v) { this.vacancies = v; return this; }

    public SearchQuery jobType(String v) { this.jobType = v; return this; }
    public SearchQuery createdBy(String v) { this.createdBy = v; return this; }

    public String getJobId() { return jobId; }
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public String getLocation() { return location; }
    public String getWorkingHours() { return workingHours; }
    public String getDateTime() { return dateTime; }
    public String getImagePath() { return imagePath; }
    public String getVacancies() { return vacancies; }

    public String getJobType() { return jobType; }
    public String getCreatedBy() { return createdBy; }
}