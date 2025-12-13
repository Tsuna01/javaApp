package model;

public interface Job {

    String getJobId();
    void setJobId(String jobId);

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
}
