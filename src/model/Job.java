package model;

public interface Job {

    int getJobId(); // เปลี่ยนเป็น int
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

    int getVacancies();
    void setVacancies(int vacancies);

    String getJobType();
    void setJobType(String jobType);

    String getImagePath();
    void setImagePath(String imagePath);

    // เพิ่ม method นี้เพราะใน JobManager มีการเรียกใช้
    int getUserId();
    void setUserId(int userId);
}