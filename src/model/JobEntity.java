package model;

public class JobEntity implements Job {

    private String jobId;
    private String title;
    private String details;
    private String location;
    private int workingHours;
    private String dateTime;
    private int vacancies;
    private String jobType;

    // ===== Constructor =====
    public JobEntity() {
    }

    public JobEntity(String jobId, String title, String details,
                     String location, int workingHours,
                     String dateTime, int vacancies, String jobType) {
        this.jobId = jobId;
        this.title = title;
        this.details = details;
        this.location = location;
        this.workingHours = workingHours;
        this.dateTime = dateTime;
        this.vacancies = vacancies;
        this.jobType = jobType;
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int getWorkingHours() {
        return workingHours;
    }

    @Override
    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    @Override
    public String getDateTime() {
        return dateTime;
    }

    @Override
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public int getVacancies() {
        return vacancies;
    }

    @Override
    public void setVacancies(int vacancies) {
        this.vacancies = vacancies;
    }

    @Override
    public String getJobType() {
        return jobType;
    }

    @Override
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
}
