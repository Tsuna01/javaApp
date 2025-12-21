package model;

public class JobEntity implements Job {

    private int jobId;
    private String title;
    private String details;
    private String location;
    private int workingHours;
    private String dateTime;
    private String endDate; // [เพิ่ม] field นี้
    private int vacancies;
    private String jobType;
    private String imagePath;
    private int userId;

    // ===== Constructor (Empty) =====
    public JobEntity() {
    }

    // ===== Constructor (Full) =====
    // [แก้ไข] เพิ่ม endDate เข้ามาใน Parameter
    public JobEntity(int jobId, String title, String details,
                     String location, int workingHours,
                     String dateTime, String endDate, int vacancies,
                     String jobType, String imagePath, int userId) {
        this.jobId = jobId;
        this.title = title;
        this.details = details;
        this.location = location;
        this.workingHours = workingHours;
        this.dateTime = dateTime;
        this.endDate = endDate; // [เพิ่ม]
        this.vacancies = vacancies;
        this.jobType = jobType;
        this.imagePath = imagePath;
        this.userId = userId;
    }

    @Override
    public int getJobId() { return jobId; }

    @Override
    public void setJobId(int jobId) { this.jobId = jobId; }

    @Override
    public String getTitle() { return title; }

    @Override
    public void setTitle(String title) { this.title = title; }

    @Override
    public String getDetails() { return details; }

    @Override
    public void setDetails(String details) { this.details = details; }

    @Override
    public String getLocation() { return location; }

    @Override
    public void setLocation(String location) { this.location = location; }

    @Override
    public int getWorkingHours() { return workingHours; }

    @Override
    public void setWorkingHours(int workingHours) { this.workingHours = workingHours; }

    @Override
    public String getDateTime() { return dateTime; }

    @Override
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    // [เพิ่ม] Getter/Setter สำหรับ End Date
    @Override
    public String getEndDate() { return endDate; }

    @Override
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @Override
    public int getVacancies() { return vacancies; }

    @Override
    public void setVacancies(int vacancies) { this.vacancies = vacancies; }

    @Override
    public String getJobType() { return jobType; }

    @Override
    public void setJobType(String jobType) { this.jobType = jobType; }

    @Override
    public String getImagePath() { return imagePath; }

    @Override
    public void setImagePath(String imagePath){ this.imagePath = imagePath; }

    @Override
    public int getUserId() { return userId; }

    @Override
    public void setUserId(int userId) { this.userId = userId; }
}