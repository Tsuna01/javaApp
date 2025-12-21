package model;

public interface Job {

    int getJobId();
    void setJobId(int jobId);

    String getTitle();
    void setTitle(String title);

    String getDetails();
    void setDetails(String details);

    String getLocation();
    void setLocation(String location);

    int getWorkingHours();
    void setWorkingHours(int workingHours);

    String getDateTime();
    void setDateTime(String dateTime);

    // [เพิ่ม] สำหรับ End Date
    String getEndDate();
    void setEndDate(String endDate);

    int getVacancies();
    void setVacancies(int vacancies);

    String getJobType();
    void setJobType(String jobType);

    String getImagePath();
    void setImagePath(String imagePath);

    int getUserId();
    void setUserId(int userId);
}